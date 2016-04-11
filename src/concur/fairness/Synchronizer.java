package concur.fairness;

/**
 * Created by coder on 04.04.16.
 */
public class Synchronizer {
    public synchronized void doSynhronized() throws InterruptedException{
        Thread.sleep(100000);
    }
}
