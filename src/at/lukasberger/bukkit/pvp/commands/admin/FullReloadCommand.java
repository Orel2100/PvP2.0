package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.commands.SubCommandManager;
import at.lukasberger.bukkit.pvp.core.*;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

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

            PvP.getInstance().getLogger().warning("Unloading loaded arenas...");
            ArenaManager.instance.unloadAllArenas();

            PvP.getInstance().getLogger().warning("Unloading loaded players...");
            PlayerManager.instance.unloadAllPlayers();

            PvP.getInstance().getLogger().warning("Stopping AFK-Task...");
            AfkManager.instance.stopTasks();

            PvP.getInstance().getLogger().warning("Removing invites...");
            InviteManager.instance.removeAll();

            PvP.getInstance().getLogger().warning("Removing parties...");
            PartyManager.instance.removeAll();

            PvP.getInstance().getLogger().warning("Unregister subcommands...");
            SubCommandManager.instance.unregisterAllSubCommands();

            PvP.getInstance().getLogger().warning("Unregister events...");
            HandlerList.unregisterAll(PvP.getInstance());

            PvP.getInstance().getLogger().fine("Reloading PvP-Configuration...");
            PvP.getInstance().reloadConfig();

            PvP.getInstance().getLogger().fine("Reloading commands...");
            PvP.getInstance().loadSubCommands();

            PvP.getInstance().getLogger().info("Saving included languages...");
            new Config("langs/de").saveDefaultConfig("lang_de");
            new Config("langs/en").saveDefaultConfig("lang");

            PvP.getInstance().getLogger().fine("Reloading default language...");
            MessageManager.instance.loadLanguage(PvP.getInstance().getConfig().getString("language"));

            sender.sendMessage(PvP.successPrefix + "PvP successfully reloaded!");
        }
        catch (Exception e)
        {
            sender.sendMessage(PvP.errorPrefix + e.getMessage());
            sender.sendMessage(PvP.errorPrefix + "Error while reloading PvP, view console for more details...");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp fullreload\n" + ChatColor.GREEN +
                        "    \u00BB\u00BB Reloads everything"
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.fullreload", "pvp.admin.*", "pvp.*");
    }

}
