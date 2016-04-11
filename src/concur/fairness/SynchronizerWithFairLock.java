package concur.fairness;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by coder on 04.04.16.
 */
public class SynchronizerWithFairLock /*extends SynhronizerWithLock*/ {
    private class QueueObject{
        private boolean isNotified;

        public synchronized void doWait() throws InterruptedException{
            while(!isNotified){
                this.wait();
            }
            this.isNotified = false;
        }

        public synchronized void doNotify(){
            this.isNotified = true;
            this.notify();
        }

        public boolean equals(Object o){
            return this == o;
        }
    }
    public class FairLock /*extends Lock*/{
        private boolean isLocked; //must be false
        private Thread lockingThread;
        private List<QueueObject> waitingThreads = new ArrayList<>();

        //@Override
        public void lock() throws InterruptedException{
            QueueObject queueObject = new QueueObject();
            boolean isLockedForThisThread = true;
            synchronized (this){
                waitingThreads.add(queueObject);
            }

            while(isLockedForThisThread){
                synchronized (this){
                    isLockedForThisThread = isLocked || waitingThreads.get(0) != queueObject;
                    if(!isLockedForThisThread){
                        isLocked = true;
                        lockingThread = Thread.currentThread();
                        waitingThreads.remove(queueObject);
                        return;
                    }
                }
                try{
                    //if two methods in class are sync - only one can be executed in specific time.
                    //synchronized don`t block me, so any thread can call unlock() anytime.
                    queueObject.doWait();
                }catch (InterruptedException e){
                    synchronized (this){waitingThreads.remove(queueObject);}
                    throw e;
                }
            }
        }

        //@Override
        public synchronized void unlock(){
            if(this.lockingThread != Thread.currentThread()){
                throw new IllegalMonitorStateException("Calling thread has not locked this lock");
            }
            isLocked = false;
            lockingThread = null;
            if(waitingThreads.size() > 0){
                waitingThreads.get(0).doNotify();
            }
        }
    }
    FairLock lock = new FairLock();
    //@Override
    public void doSynhronized() throws InterruptedException{
        this.lock.lock();
        Thread.sleep(100000);
        this.lock.unlock();
    }
}
