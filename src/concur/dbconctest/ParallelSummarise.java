package concur.dbconctest;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by coder on 10.04.16.
 */
public class ParallelSummarise{
    private class Container{
        private long sum;
        public Container(int val1, int val2){
            this.sum = val1+val2;
        }
        public long getSum(){
            return this.sum;
        }
    }
    
    private class TaskFillQueue implements Runnable{
        /**
         Only one thread, no obvious blocking is needed
         */
        @Override
        public void run(){
            int counter = 0;
            try{
                while (rs.next()) {
                    int val1 = rs.getInt(1);
                    int val2 = rs.getInt(2);
                    containerQueue.put(new Container(val1, val2));
                    counter++;
                    if (counter != 0 && (counter % 10000 == 0)) {
                        System.out.println(counter + " objects contained");
                    }
                }
                while(containerQueue.size() != 0){
                    Thread.sleep(1000);
                }
                
            }catch(SQLException | InterruptedException e){e.printStackTrace();}
            isNext = false;
            System.out.println(Thread.currentThread().getName() + " terminated");
        }
    }
    
    private class TaskProcessQueue implements Runnable{
    /**
     multiply threads
     */
        @Override
        public void run(){
            while (!containerQueue.isEmpty() | isNext) {
                boolean updated;
                int counter;
                try {
                    long sum = containerQueue.take().getSum();
                    lock.lock();
                    ps.setNull(1, Types.INTEGER);
                    ps.setLong(2, sum);
                    ps.setBoolean(3, false);
                    ps.executeUpdate();
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
                do {
                    counter = rowsSet.get();
                    updated = rowsSet.compareAndSet(counter, ++counter);
                } while (!updated);
                if (counter % 10000 == 0) {
                    System.out.println(Thread.currentThread().getName() + " : " + counter);
                    //conn.commit();
                }
                
            }
        }
    }

    private final PreparedStatement ps;
    private final ResultSet rs;
    private final Connection conn;//is it needed?
    private final int corePoolSize = 5;
    private final int maxPoolSize = 10;
    private final long keepAliveTime = 5000;
    private final Lock lock = new ReentrantLock();
    private BlockingQueue<Container> containerQueue = new ArrayBlockingQueue<>(10000);
    private AtomicInteger rowsSet = new AtomicInteger(0);
    private boolean isNext = true;
    

    public ParallelSummarise(Connection conn, PreparedStatement ps, ResultSet rs){
        this.conn = conn;
        this.ps = ps;
        this.rs = rs;
    }

    public void exec(){
        Thread filler = new Thread(new TaskFillQueue());
        filler.setName("Filler Thread");
        filler.start();
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i<maxPoolSize;i++){
            threadPoolExecutor.execute(new TaskProcessQueue());
        }
        //ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
        //threadPoolExecutor.execute(new Task());
        while(isNext){
            try{
                Thread.sleep(5000);
                System.out.println("waiting...");
            }catch (InterruptedException e){e.printStackTrace();}
        }
        threadPoolExecutor.shutdown();
    }
}