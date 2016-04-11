package test.StaticProcOverride;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;

/**
 * Created by coder on 02.04.16.
 */
public class RegularEmployee {
    private BigDecimal salary = BigDecimal.ONE;

    public void setSalary(BigDecimal salary){
        this.salary = salary;
    }

    public static BigDecimal getBonusMultiplier(){
        return new BigDecimal("0.2");
    }

    public BigDecimal calculateBonus(){
        return salary.multiply(this.getBonusMultiplier());
    }
    /**
     * reflection using
     * */
    public BigDecimal calculateOverrideBonus(){
        try{
            System.out.println(this.getClass().getDeclaredMethod("getBonusMultiplier").toString());
            try{
                return salary.multiply((BigDecimal) this.getClass().getDeclaredMethod("getBonusMultiplier").invoke(this));
            }catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e){}
        }catch (NoSuchMethodException | SecurityException e){}

        return null;
    }
}
