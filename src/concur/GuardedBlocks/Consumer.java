package concur.GuardedBlocks;

/**
 * Created by coder on 20.03.16.
 */
import java.util.Random;
public class Consumer implements Runnable {
    private Drop drop;

    public Consumer(Drop drop){this.drop = drop;}

    @Override
    public void run(){
        Random rand = new Random();
        while(true){
            String message = drop.take();
            if(message.equals("DONE")){break;}
            System.out.format("RECEIVED MESSAGE: %S%n",message);
            try {
                Thread.sleep(rand.nextInt(5000));
            }catch(InterruptedException ex){}
        }
    }
}
