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
public class SpectateCommand extends AbstractSubCommand
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
            InGameManager.instance.joinArenaSpectating((Player)sender, args[0]);
        }
        else
        {
            sender.sendMessage(ChatColor.AQUA + "~~~ PvP: Spectate ~~~");
            sender.sendMessage(ChatColor.GRAY + "/pvp spec/s {Arena}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.spectate"));
        }
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp spec/s {Arena}\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.spectate")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.spectate", "pvp.player", "pvp.player.*", "pvp.*");
    }

}
