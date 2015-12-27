package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.messages.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class JoinCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
            return;

        if(args.length == 1)
        {
            InGameManager.instance.joinArena((Player)sender, args[0]);
        }
        else
        {
            sender.sendMessage(ChatColor.AQUA + "~~~ PvP: Join ~~~");
            sender.sendMessage(ChatColor.GRAY + "/pvp join/j {Arena}\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.join"));
        }
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player", "pvp.*");
    }

}