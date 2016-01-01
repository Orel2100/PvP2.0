package at.lukasberger.bukkit.pvp.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class BukkitUtils
{

    public static boolean isPlayer(CommandSender sender)
    {
        return (sender instanceof Player);
    }

}
