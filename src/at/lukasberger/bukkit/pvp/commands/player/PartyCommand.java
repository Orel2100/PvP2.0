package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.PartyManager;
import at.lukasberger.bukkit.pvp.core.messages.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PartyCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
            return;

        if(args.length == 1)
        {
            if(args[0].equalsIgnoreCase("create"))
            {
                PartyManager.instance.create((Player)sender);
            }
            else if(args[0].equalsIgnoreCase("delete"))
            {
                PartyManager.instance.delete((Player)sender);
            }
            else
            {
                printHelp(sender);
            }
        }
        else if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("leader"))
            {
                PartyManager.instance.changeLeader((Player)sender, args[1]);
            }
            else
            {
                printHelp(sender);
            }
        }
        else
        {
            printHelp(sender);
        }

        InGameManager.instance.leaveArena((Player)sender);
    }

    private void printHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP: Party ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p create\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.create"));
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p delete\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.delete"));
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p invite [Player]\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.invite"));
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p accept [Player]\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.accept"));
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p deny [Player]\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.deny"));
        sender.sendMessage(ChatColor.GRAY + "/pvp party/p leader [Player]\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.party.leader"));
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.party", "pvp.player", "pvp.player.*", "pvp.*");
    }

}
