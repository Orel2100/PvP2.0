package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PlayerLanguageCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(!(sender instanceof Player))
        {
            sender.sendMessage(PvP.errorPrefix + "Player-only command!");
            return;
        }

        if(args.length == 0)
        {
            printHelp(sender);
        }
        else if(args.length == 1)
        {
            String language = args[0];
            Config langConfig = new Config("langs/" + language);

            if(langConfig.exists())
            {
                InGameManager.instance.getPlayer((Player)sender).setLanguage(language);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.language.player.set"));
            }
            else
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.language.player.not-existing"));
        }
        else
        {
            printHelp(sender);
        }
    }

    private void printHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP-Admin: Language ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp lang {Language}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.language.player"));
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp lang {Language}\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.language.player")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.language", "pvp.player", "pvp.player.*", "pvp.*");
    }

}
