package concur.GuardedBlocks;

/**
 * Created by coder on 20.03.16.
 */
public class Drop {
    private String message;
    private boolean empty = true;

    public synchronized String take(){
        while(empty){
            try {
                wait();
            }catch(InterruptedException ex){}
        }
        empty = true;
        notifyAll();
        return message;
    }

    public synchronized void put(String message){
        while(!empty){
            try{
                wait();
            }catch(InterruptedException ex){}
        }
        empty = false;
        this.message = message;
        notifyAll();
    }

}
