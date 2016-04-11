package test.StaticProcOverride;

/**
 * Created by coder on 02.04.16.
 */
public class Runner {
    public static void main(String[] args) {
        RegularEmployee alan = new RegularEmployee();
        System.out.println(alan.calculateBonus());
        System.out.println(alan.calculateOverrideBonus());
        SpecialEmployee bob = new SpecialEmployee();
        System.out.println(bob.calculateBonus());
        System.out.println(bob.calculateOverrideBonus());
        RegularEmployee helen = new SpecialEmployee();
        System.out.println(helen.calculateBonus());
        System.out.println(helen.calculateOverrideBonus());
    }
}
