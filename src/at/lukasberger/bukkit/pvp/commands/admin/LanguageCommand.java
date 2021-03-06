package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class LanguageCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            printHelp(sender);
        }
        else if(args.length == 2)
        {
            if(args[0].equalsIgnoreCase("set"))
            {
                String langName = args[1];
                Config langConfig = new Config("langs/" + langName);

                if(!langConfig.exists())
                {
                    sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "action.language.not-existing"));
                }
                else
                {
                    PvP.getInstance().getConfig().set("language", langName);
                    PvP.getInstance().saveConfig();

                    sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "action.language.changed", langName));
                }
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
    }

    private void printHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP-Admin: Language-Admin ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp langa set {Name}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.language.set"));
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp langa set {Name}\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.language.set")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.lang", "pvp.admin.*", "pvp.*");
    }

}
