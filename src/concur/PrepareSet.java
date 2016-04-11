package concur;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by coder on 12.03.16.
 */
public class PrepareSet {

    public ArrayList<Integer> sideOne;
    public ArrayList<Integer> sideTwo;
    public ArrayList<Integer> product;
    private final int count;
    private int pointer = 0;

    public PrepareSet(int count){
        this.count = count;
        this.sideOne = new ArrayList<>();
        this.sideTwo = new ArrayList<>();
        this.product = new ArrayList<>();
        //Random rand = new Random();
        for(int i = 0; i < count; i++){
            sideOne.add(count+1);
            sideTwo.add(count-1);
        }
    }

    public synchronized void operation(){
        int prod = sideOne.get(pointer)*sideTwo.get(pointer);
        product.add(pointer,prod);
        System.out.println("Operation "+pointer+" product "+product.get(pointer));
        if(pointer == count){
            notifyAll();
        }
        pointer++;
    }

    public int getCount(){return count;}

    public int getPointer(){return pointer;}
}
