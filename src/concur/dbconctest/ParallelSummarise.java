package concur.dbconctest;

import java.sql.*;
import java.util.concurrent.*;

/**
 * Created by coder on 10.04.16.
 */
public class ParallelSummarise extends Thread{
    private class Pair{
        private int sum;
        public Pair(int val1,int val2){
            this.sum = val1 + val2;
        }

        public long getSum(){
            return this.sum;
        }
    }
    private class TaskInput implements Runnable{
        @Override
        public void run(){
            int val1,val2;
            try{
                while(rs.next()){

                        val1 = rs.getInt(1);
                        val2 = rs.getInt(2);
                        queue.put(new Pair(val1, val2));

                        /*ps.setNull(1, Types.INTEGER);
                        ps.setLong(2, val1 + val2);
                        ps.setBoolean(3, false);
                        ps.executeUpdate();
                        if((rowsSet % 5000) == 0 && rowsSet !=0){
                            System.out.println(Thread.currentThread().getName()+" : "+rowsSet);
                            //conn.commit();
                        }
                        rowsSet++;*/
                }
                //isNext = false;
            }catch (SQLException | InterruptedException e){e.printStackTrace();}
        }
    }
    private class TaskOutput implements Runnable{
        @Override
        public void run(){
            try{
                ps.setNull(1,Types.INTEGER);

                    ps.setLong(2, queue.take().getSum());

                ps.setBoolean(3, false);
                ps.executeUpdate();
                if((rowsSet % 5000) == 0 && rowsSet !=0){
                    System.out.println(Thread.currentThread().getName()+" : "+rowsSet);
                    //conn.commit();
                }
                rowsSet++;
                if(queue.size()==0){isNext = false;}
            }catch (SQLException | InterruptedException e){e.printStackTrace();}
        }
    }
    private PreparedStatement ps;
    volatile private ResultSet rs;
    private Connection conn;
    volatile private int rowsSet;
    private BlockingQueue<Pair> queue;
    private final int corePoolSize = 5;
    private final int maxPoolSize = 10;
    private final long keepAliveTime = 5000;
    private boolean isNext = true;

    public ParallelSummarise(Connection conn, PreparedStatement ps, ResultSet rs){
        this.conn = conn;
        this.ps = ps;
        this.rs = rs;
    }

    @Override
    public void run(){
        queue = new ArrayBlockingQueue<>(5000);
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        for (int i = 0; i<maxPoolSize;i++){
            threadPoolExecutor.execute(new TaskInput());
            threadPoolExecutor.execute(new TaskOutput());
        }
        //ExecutorService threadPoolExecutor = Executors.newSingleThreadExecutor();
        //threadPoolExecutor.execute(new TaskInput());
        while(isNext){
            try{
                Thread.sleep(5000);
                System.out.println("waiting...");
            }catch (InterruptedException e){e.printStackTrace();}
        }
        threadPoolExecutor.shutdown();
    }
}
