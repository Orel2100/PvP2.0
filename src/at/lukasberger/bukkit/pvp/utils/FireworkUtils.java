package at.lukasberger.bukkit.pvp.utils;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class FireworkUtils
{

    private static Random rand = new Random();

    public static void launchRandomFirework(Location loc, Integer count)
    {
        for(int i = 0; i < count; i++)
            getRandomFirework(loc).eject();
    }

    public static Firework getRandomFirework(Location loc)
    {
        Firework fw = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        MapTuple<Color, Color> randomColors = getRandomFireworkColors();
        FireworkEffect.Type randomType = getRandomFireworkType();

        FireworkEffect effect = FireworkEffect.builder()
                .flicker(rand.nextBoolean()).trail(rand.nextBoolean())
                .withColor(randomColors.key()).withFade(randomColors.value())
                .with(randomType).build();

        fwm.addEffect(effect);
        fwm.setPower(rand.nextInt(2) + 1);
        fw.setFireworkMeta(fwm);
        fw.eject();

        return fw;
    }

    private static FireworkEffect.Type getRandomFireworkType()
    {
        int randNum = rand.nextInt(5);

        if(randNum == 1)
            return FireworkEffect.Type.BALL_LARGE;
        else if(randNum == 2)
            return FireworkEffect.Type.BURST;
        else if(randNum == 3)
            return FireworkEffect.Type.CREEPER;
        else if(randNum == 4)
            return FireworkEffect.Type.STAR;
        else
            return FireworkEffect.Type.BALL;
    }

    private static MapTuple<Color, Color> getRandomFireworkColors()
    {
        int randNum1 = rand.nextInt(17) + 1;
        int randNum2 = rand.nextInt(17) + 1;

        return new MapTuple<>(getColor(randNum1), getColor(randNum2));
    }

    private static Color getColor(int i)
    {
        if(i == 1)
            return Color.AQUA;
        else if(i == 2)
            return Color.BLACK;
        else if(i == 3)
            return Color.BLUE;
        else if(i == 4)
            return Color.FUCHSIA;
        else if(i == 5)
            return Color.GRAY;
        else if(i == 6)
            return Color.GREEN;
        else if(i == 7)
            return Color.LIME;
        else if(i == 8)
            return Color.MAROON;
        else if(i == 9)
            return Color.NAVY;
        else if(i == 10)
            return Color.OLIVE;
        else if(i == 11)
            return Color.ORANGE;
        else if(i == 12)
            return Color.PURPLE;
        else if(i == 13)
            return Color.RED;
        else if(i == 14)
            return Color.SILVER;
        else if(i == 15)
            return Color.TEAL;
        else if(i == 16)
            return Color.WHITE;
        else if(i == 17)
            return Color.YELLOW;
        else
            return Color.WHITE;
    }

}
