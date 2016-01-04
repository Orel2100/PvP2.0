package at.lukasberger.bukkit.pvp.utils;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class MathUtils
{

    public static double round(double d, int digits)
    {
        BigDecimal bd = new BigDecimal(d);
        bd = bd.round(new MathContext(digits));
        return bd.doubleValue();
    }

}
