package concur.dbconctest;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by coder on 10.04.16.
 */
public class ParallelSummarise{
    private final class Container{
        private final long sum;
        public Container(int val1, int val2){
            this.sum = val1+val2;
        }
        public long getSum(){
            return this.sum;
        }
    }

    private class TaskFillQueue implements Runnable{
        private ResultSet rs;
        private BlockingQueue<Container> containerQueue;

        public TaskFillQueue(ResultSet rs, BlockingQueue<Container> containerQueue){
            this.rs = rs;
            this.containerQueue = containerQueue;
        }
        /**
         Only one thread, no obvious blocking is needed
         */
        @Override
        public void run(){
            int counter = 0;
            try{
                while (rs.next()) {
                    containerQueue.put(new Container(rs.getInt(1), rs.getInt(2)));
                    counter++;
                    if (counter != 0 && (counter % 100000 == 0)) {
                        System.out.println(counter + " objects contained");
                    }
                }
                while(!containerQueue.isEmpty()){
                    Thread.sleep(1000);
                }
            }catch(SQLException | InterruptedException e){e.printStackTrace();}
            isNext = false;
            System.out.println(Thread.currentThread().getName() + " terminated");
        }
    }

    private class TaskProcessQueue implements Runnable{
        private BlockingQueue<Container> containerQueue;
        private String queryInsertValues;
        private Connection conn;

        public TaskProcessQueue(BlockingQueue<Container> containerQueue,
                                String queryInsertValues, Connection conn){
            this.containerQueue = containerQueue;
            this.queryInsertValues = queryInsertValues;
            this.conn = conn;
        }
        /**
         multiply threads
         */
        @Override
        public void run(){
            boolean updated;
            int counter;
            int i = 1;
            try(PreparedStatement ps = conn.prepareStatement(queryInsertValues)) {
                while (!containerQueue.isEmpty()) {
                    try {
                        long sum = containerQueue.take().getSum();
                        ps.setLong(1, sum);
                        ps.setBoolean(2, false);
                        ps.addBatch();
                        i++;
                        if(i % 1000 == 0 || containerQueue.isEmpty()){
                            ps.executeBatch();
                        }
                    }catch (SQLException | InterruptedException e) {e.printStackTrace();}
                    do {
                        counter = rowsSet.get();
                        updated = rowsSet.compareAndSet(counter, ++counter);
                    } while (!updated);
                    if (counter % 100000 == 0) {
                        System.out.println(Thread.currentThread().getName() + " : " + counter);
                        conn.commit();
                    }
                }
            }catch (SQLException e){e.printStackTrace();}
        }
    }
    private final Connection conn;
    private final String queryInsertValues;
    private final ResultSet rs;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final BlockingQueue<Container> containerQueue;
    private final AtomicInteger rowsSet;
    private boolean isNext;


    public ParallelSummarise(String queryInsertValues, ResultSet rs, Connection conn){
        this.queryInsertValues = queryInsertValues;
        this.rs = rs;
        this.conn = conn;
        this.corePoolSize = 5;
        this.maxPoolSize = 10;
        this.keepAliveTime = 5000;
        this.containerQueue = new ArrayBlockingQueue<>(10000);
        this.rowsSet = new AtomicInteger(0);
        this.isNext = true;
    }

    public void exec()throws SQLException{
        Thread filler = new Thread(new TaskFillQueue(rs,containerQueue));
        filler.setName("Filler Thread");
        filler.start();
        ExecutorService threadPoolExecutor = null;
        try{
            conn.setAutoCommit(false);
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            for (int i = 0; i < maxPoolSize; i++) {
                threadPoolExecutor.execute(new TaskProcessQueue(containerQueue, queryInsertValues, conn));
            }
            //ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
            //threadPoolExecutor.execute(new Task());
            while (isNext) {
                try {
                    Thread.sleep(5000);
                    //System.out.println("waiting...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch(SQLException e){
            e.printStackTrace();
            conn.rollback();
        }finally{
            conn.commit();
            conn.setAutoCommit(true);
            threadPoolExecutor.shutdown();
        }
    }
}
