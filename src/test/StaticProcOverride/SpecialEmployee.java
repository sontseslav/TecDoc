package test.StaticProcOverride;

import java.math.BigDecimal;

/**
 * Created by coder on 02.04.16.
 */
public class SpecialEmployee extends RegularEmployee {
    public static BigDecimal getBonusMultiplier(){
        return new BigDecimal("0.3");
    }
}
