package concur.deadlock;

/**
 * Created by coder on 03.04.16.
 */
public class SimpleDeadLock {
    private static class SharedObject{
        private String name;

        public SharedObject(String name){
            this.name = name;
        }

        public void possess(){
            System.out.println(name+": someone had possessed me...");
            try {
                Thread.sleep(50);
            }catch (InterruptedException e){}
            System.out.println(name+": someone had release me...");
        }
    }

    private static class TestThread implements Runnable{
        private SharedObject resourceA;
        private SharedObject resourceB;

        public TestThread(SharedObject resourceA, SharedObject resourceB){
            this.resourceA = resourceA;
            this.resourceB = resourceB;
        }

        public void run(){
            synchronized (resourceA) {//grub resource A
                System.out.println(Thread.currentThread().getName() + ": Possessing " + resourceA.name);
                resourceA.possess();
                System.out.println(Thread.currentThread().getName() + ": Done " + resourceA.name);
                synchronized (resourceB) {//I don`t want to release A, but want grub resource b
                    System.out.println(Thread.currentThread().getName() + ": Possessing " + resourceB.name);
                    resourceB.possess();
                    System.out.println(Thread.currentThread().getName() + ": Done " + resourceB.name);
                }
            }
        }
    }

    public static void main(String[] args) {
        SharedObject resA = new SharedObject("A");
        SharedObject resB = new SharedObject("B");
        new Thread(new TestThread(resA,resB)).start();
        new Thread(new TestThread(resB,resA)).start();
    }
}
