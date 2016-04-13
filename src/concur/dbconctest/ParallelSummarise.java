package concur.dbconctest;

import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by coder on 10.04.16.
 */
public class ParallelSummarise{
    private class Task implements Runnable{
        @Override
        public void run(){
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
    private final Connection conn;
    private int rowsSet;
    private final int corePoolSize = 5;
    private final int maxPoolSize = 10;
    private final long keepAliveTime = 5000;
    private boolean isNext = true;
    private final Lock lock = new ReentrantLock();

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