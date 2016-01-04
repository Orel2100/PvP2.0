package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class LeaveCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(PvP.errorPrefix + "Player-only command!");
            return;

        }

        Player p = (Player)sender;

        if(InGameManager.instance.isPlayerIngame(p))
            InGameManager.instance.leaveArena(p);
        else if(InGameManager.instance.isPlayerSpectating(p))
            InGameManager.instance.leaveArenaSpectating(p);
        else if(InGameManager.instance.getArena(p).isPlayerInQueue(p))
            InGameManager.instance.getArena(p).leaveFromQueue(p);
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp leave/l {Arena}\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.leave")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player", "pvp.player.*", "pvp.*");
    }

}
