package concur;

/**
 * Created by coder on 12.03.16.
 */
public class Runner {
    public static void main(String[] args){
        PrepareSet ps = new PrepareSet(100000);
        ExecThread r1 = new ExecThread(ps);
        ExecThread r2 = new ExecThread(ps);
        Thread thread1 = new Thread(r1);
        Thread thread2 = new Thread(r2);
        thread1.start();
        thread2.start();
    }
}
