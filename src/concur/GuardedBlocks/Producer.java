package concur.GuardedBlocks;

/**
 * Created by coder on 20.03.16.
 */
import java.util.Random;
public class Producer implements Runnable {
    private Drop drop;

    public Producer(Drop drop){
        this.drop = drop;
    }

    @Override
    public void run(){
        String[] importatnInfo = {
                "Mares eat oats",
                "Does eat oats",
                "Little lambs eat ivy",
                "A kid will eat ivy too"
        };
        Random rand = new Random();
        for(int i = 0; i < importatnInfo.length; i++){
            drop.put(importatnInfo[i]);
            try{Thread.sleep(rand.nextInt(5000));}catch (InterruptedException ex){}
        }
        drop.put("DONE");
    }
}
