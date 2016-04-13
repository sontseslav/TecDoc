package concur.dbconctest;

import java.sql.*;
import java.util.concurrent.*;
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
                    System.out.println(++counter + " objects added to container");
                }
            }catch(SQLException | InterruptedException e){e.printStackTrace();}
        }
    }
    private class Task implements Runnable{
        @Override
        public void run(){//runs 138 k miliseconds
            int val1,val2;
            
                while(true){
                    //synchronized(this){
                    lock.lock();
                    try{
                        boolean isResultPresent = rs.next();
                        if (!isResultPresent) {
                            break;
                        }
                        val1 = rs.getInt(1);
                        val2 = rs.getInt(2);
                        lock.unlock();
                        lock.lock();
                        ps.setNull(1, Types.INTEGER);
                        ps.setLong(2, val1 + val2);
                        ps.setBoolean(3, false);
                        ps.executeUpdate();
                        if ((rowsSet % 5000) == 0 && rowsSet != 0) {
                            System.out.println(Thread.currentThread().getName() + " : " + rowsSet);
                            //conn.commit();
                        }
                        rowsSet++;
                    }catch(SQLException e){
                        e.printStackTrace();
                    }finally{
                        lock.unlock();
                    }
                    //}
                }
                /*while(rs.next()){
                    val1 = rs.getInt(1);
                    val2 = rs.getInt(2);
                    ps.setNull(1, Types.INTEGER);
                    ps.setLong(2, val1 + val2);
                    ps.setBoolean(3, false);
                    ps.executeUpdate();
                    if((rowsSet % 5000) == 0 && rowsSet !=0){
                        System.out.println(Thread.currentThread().getName()+" : "+rowsSet);
                        //conn.commit();
                    }
                    rowsSet++;
                }*/
                isNext = false;
            
        }
    }
    private final PreparedStatement ps;
    private final ResultSet rs;
    private final Connection conn;//is it needed?
    private final int corePoolSize = 5;
    private final int maxPoolSize = 10;
    private final long keepAliveTime = 5000;
    private final Lock lock = new ReentrantLock();
    private BlockingQueue<Container> containerQueue = new ArrayBlockingQueue<>(1024);
    private int rowsSet;
    private boolean isNext = true;
    

    public ParallelSummarise(Connection conn, PreparedStatement ps, ResultSet rs){
        this.conn = conn;
        this.ps = ps;
        this.rs = rs;
    }

    public void exec(){
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for (int i = 0; i<maxPoolSize;i++){
            threadPoolExecutor.execute(new Task());
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