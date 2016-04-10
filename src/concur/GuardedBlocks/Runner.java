package concur.GuardedBlocks;

/**
 * Created by coder on 20.03.16.
 */
public class Runner {
    public static void main(String... args){
        Drop drop = new Drop();
        new Thread(new Producer(drop)).start();
        new Thread(new Consumer(drop)).start();
    }
}
