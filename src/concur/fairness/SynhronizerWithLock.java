package concur.fairness;

import java.util.concurrent.locks.Lock;

/**
 * Created by coder on 04.04.16.
 */
public class SynhronizerWithLock extends Synchronizer {
    /*
    * So, the current version of the Lock class makes no different
    * guarantees with respect to fairness than synchronized version of doSynchronized().
    */
    public class Lock{
        private boolean isLocked; //must be false
        private Thread lockingThread;

        public synchronized void lock() throws InterruptedException{
            while(isLocked){
                //wait() releases synchronisation lock - multiply threads may be here
                wait();
            }
            isLocked = true;
            lockingThread = Thread.currentThread();
        }

        public synchronized void unlock(){
            if(Thread.currentThread() != this.lockingThread){
                throw new IllegalMonitorStateException("Calling thread has not locked this lock");
            }
            isLocked = false;
            lockingThread = null;
            notify();
        }
    }

    Lock lock = new Lock();

    @Override
    public void doSynhronized() throws InterruptedException{
        this.lock.lock();
        Thread.sleep(100000);
        this.lock.unlock();
    }
}
