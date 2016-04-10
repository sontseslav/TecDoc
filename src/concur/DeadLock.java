package concur;

/**
 * Created by coder on 20.03.16.
 */
public class DeadLock {
    private static class Friend{
        private String name;
        public String getName(){
            return this.name;
        }
        public Friend(String name){
            this.name = name;
        }
        public synchronized void bow(Friend bower){
            System.out.format("%s: %s has bowed to me!%n",
                    this.name, bower.getName());
            bower.bowBack(this);
        }
        public synchronized void bowBack(Friend bower){
            System.out.format("%s: %s has bowed back to me!%n",
                    this.name, bower.getName());
        }
    }

    public static void main(String... args){
        final Friend antuan = new Friend("Antuan");
        final Friend jan = new Friend("Jan");
        new Thread(new Runnable() {
            @Override
            public void run() {
                antuan.bow(jan);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                jan.bow(antuan);
            }
        }).start();
    }
}
