package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.InviteManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class EloCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            if(!(sender instanceof Player))
            {
                sender.sendMessage(PvP.errorPrefix + "Player-only command!");
                return;
            }

            Integer elo = InGameManager.instance.getPlayer((Player)sender).getElo();

            if(elo == Integer.MIN_VALUE)
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.elo.no-personal-elo"));
            else
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.elo.personal-elo", elo));
        }
        else if(args.length == 1)
        {
            Player p = PvP.getInstance().getServer().getPlayer(args[1]);
            Integer elo = InGameManager.instance.getPlayer(p).getElo();

            if(p == null || !p.isOnline())
            {
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.elo.not-online"));
                return;
            }

            if(elo == Integer.MIN_VALUE)
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.elo.no-other-elo"));
            else
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.elo.other-elo", p.getName(), elo));
        }
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp elo [Name]\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.elo")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.invite", "pvp.player.*", "pvp.*");
    }

}
