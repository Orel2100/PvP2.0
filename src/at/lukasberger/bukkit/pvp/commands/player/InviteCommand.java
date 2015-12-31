package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InviteManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class InviteCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(PvP.errorPrefix + "Player-only command!");
            return;
        }

        if(args.length == 1)
        {
            InviteManager.instance.invite((Player)sender, args[0]);
        }
        else
        {
            sender.sendMessage(ChatColor.AQUA + "~~~ PvP: Invite ~~~");
            sender.sendMessage(ChatColor.GRAY + "/pvp invite/i {Name}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.invite"));
            sender.sendMessage(ChatColor.GRAY + "/pvp accept/a {Name}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.accept"));
            sender.sendMessage(ChatColor.GRAY + "/pvp deny/d {Name}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.deny"));
        }
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.invite", "pvp.player", "pvp.*");
    }

}
