/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package concur;

/**
 *
 * @author user
 */
public class Runner {
    public static void main(String... args){
        JobMaker r1 = new JobMaker(15);
        Thread thr1 = new Thread(r1);
        thr1.start();
        JobMaker r2 = new JobMaker(5);
        Thread thr2 = new Thread(r2);
        thr2.start();
                
    }
}
