package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class FullReloadCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        sender.sendMessage(PvP.prefix + "Fully reloading PvP...");

        try
        {
            PvP.getInstance().getLogger().warning("Kicking all players from arena...");
            InGameManager.instance.leaveArenaAll();

            PvP.getInstance().getLogger().info("Reloading PvP-Configuration...");
            PvP.getInstance().reloadConfig();

            PvP.getInstance().getLogger().info("Unloading loaded arenas...");
            ArenaManager.instance.unloadAllArenas();

            PvP.getInstance().getLogger().info("Unloading loaded players...");
            PlayerManager.instance.unloadAllPlayers();

            PvP.getInstance().getLogger().info("Removing invites...");
            InviteManager.instance.removeAll();

            PvP.getInstance().getLogger().info("Removing parties...");
            PartyManager.instance.removeAll();

            PvP.getInstance().getLogger().info("Reloading language...");
            MessageManager.instance.loadLanguage(PvP.getInstance().getConfig().getString("language"));

            sender.sendMessage(PvP.successPrefix + "PvP successfully reloaded!");
        }
        catch (Exception e)
        {
            sender.sendMessage(PvP.errorPrefix + "Error while reloading PvP, view console for more details...");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.fullreload", "pvp.admin", "pvp.*");
    }

}
