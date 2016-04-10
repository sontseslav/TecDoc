package concur.dbconctest;

import java.util.concurrent.*;

/**
 * Created by coder on 10.04.16.
 */
public class Test {
    int corePoolSize = 5;
    int maxPoolSize = 10;
    long keepAliveTime = 5000;
    volatile int  index = 0;

    public static void main(String[] args) throws InterruptedException{
        new Test().exec();
    }
    public void exec() throws InterruptedException{
        ExecutorService threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        for(int i=0;i<maxPoolSize*10;i++) {
            threadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println("hello! " + (index++));
                    try {
                        Thread.sleep(5000);
                    }catch(InterruptedException e){}
                }
            });
        }

        threadPoolExecutor.shutdown();

    }

}
