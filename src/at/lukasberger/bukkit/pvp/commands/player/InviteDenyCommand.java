package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InviteManager;
import at.lukasberger.bukkit.pvp.core.messages.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;


/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class InviteDenyCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
            return;

        if(args.length == 1)
        {
            InviteManager.instance.deny((Player)sender, args[1]);
        }
        else
        {
            sender.sendMessage(ChatColor.AQUA + "~~~ PvP: Invite ~~~");
            sender.sendMessage(ChatColor.GRAY + "/pvp invite/i {Name}\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.invite"));
            sender.sendMessage(ChatColor.GRAY + "/pvp accept/a {Name}\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.accept"));
            sender.sendMessage(ChatColor.GRAY + "/pvp deny/d {Name}\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.deny"));
        }
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player", "pvp.*");
    }

}
