package concur;


/**
 * Created by coder on 12.03.16.
 */
public class ExecThread implements Runnable {

    private static final long time;
    private PrepareSet prepareSet;

    static{
        time = System.currentTimeMillis();
    }

    public ExecThread(PrepareSet prepareSet){
        this.prepareSet = prepareSet;
    }

    @Override
    public void run() {
        while (prepareSet.getPointer() < prepareSet.getCount()) {
            System.out.println(Thread.currentThread().getName() + " start operation");
            prepareSet.operation();
            System.out.println(Thread.currentThread().getName() + " stop operation." +
                    "Elapsed time: " + (System.currentTimeMillis() - time));
        }
    }
}
