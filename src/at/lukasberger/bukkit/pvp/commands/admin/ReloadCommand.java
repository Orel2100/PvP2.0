package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.PlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class ReloadCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        sender.sendMessage(PvP.prefix + "Reloading PvP...");

        try
        {
            PvP.getInstance().getLogger().info("Reloading PvP-Configuration...");
            PvP.getInstance().reloadConfig();

            sender.sendMessage(PvP.successPrefix + "PvP successfully reloaded!");
            sender.sendMessage(PvP.warningPrefix + "We recommend to use /pvp fullreload to reload all configurations");
        }
        catch (Exception e)
        {
            sender.sendMessage(PvP.errorPrefix + "Error while reloading PvP, view console for more details...");
            e.printStackTrace();
        }
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp fullreload\n" + ChatColor.GREEN +
                        "    \u00BB\u00BB Reloads configuration only"
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.reload", "pvp.admin.*", "pvp.*");
    }

}
