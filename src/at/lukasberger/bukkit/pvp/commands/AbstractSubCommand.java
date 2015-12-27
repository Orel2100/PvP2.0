package at.lukasberger.bukkit.pvp.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public abstract class AbstractSubCommand
{

    public abstract void execute(CommandSender sender, String[] args);

    public abstract List<String> getPermissions();

}
