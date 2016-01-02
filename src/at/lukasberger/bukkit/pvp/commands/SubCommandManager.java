package at.lukasberger.bukkit.pvp.commands;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.*;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class SubCommandManager
{

    private HashMap<String, AbstractSubCommand> subCommands = new HashMap<>();
    public static SubCommandManager instance = new SubCommandManager();

    private SubCommandManager() { }

    public void registerSubCommand(AbstractSubCommand cmdInst, String... commands)
    {
        for(String cmd : commands)
            registerSubCommand(cmdInst, cmd);
    }

    public void registerSubCommand(AbstractSubCommand cmdInst, String cmd)
    {
        if(!subCommands.containsKey(cmd))
            subCommands.put(cmd, cmdInst);
        else
            throw new IllegalArgumentException("The sub-command " + cmd + " is already registered");
    }

    public void unregisterSubCommand(String cmd)
    {
        subCommands.remove(cmd);
    }

    public void unregisterAllSubCommands()
    {
        subCommands.clear();
    }

    public boolean executeSubCommand(CommandSender sender, String[] args)
    {
        List<String> tmpArgs = new ArrayList<>(Arrays.asList(args));

        String subCmdString = tmpArgs.get(0);
        tmpArgs.remove(0);

        if(subCmdString.equalsIgnoreCase("help"))
        {
            Integer baseIndex = 1;

            try
            {
                baseIndex = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                baseIndex = 1;
            }

            Integer startIndex = (baseIndex * 4) - 4;
            Integer currIndex = 0;

            List<String> help = new ArrayList<>();

            for(Map.Entry<String, AbstractSubCommand> subcmd : subCommands.entrySet())
            {
                boolean hasPerm = false;

                for(String perm : subcmd.getValue().getPermissions())
                    if(sender.hasPermission(perm))
                        hasPerm = true;

                if(hasPerm)
                {
                    for(String line : subcmd.getValue().getHelp(sender))
                        if(!help.contains(line))
                            help.add(line);
                }
            }

            for(String helpLine : help)
            {
                if(currIndex < startIndex)
                {
                    currIndex++;
                    continue;
                }

                sender.sendMessage(helpLine);

                currIndex++;
                if(currIndex == (baseIndex * 4))
                    break;
            }

            if(subCommands.size() > currIndex - 4)
                sender.sendMessage(PvP.prefix + ChatColor.GRAY + MessageManager.instance.get(sender, "commands.help.help-next", baseIndex + 1));

            return true;
        }

        for(Map.Entry<String, AbstractSubCommand> subcmd : subCommands.entrySet())
        {
            if(subcmd.getKey().equalsIgnoreCase(subCmdString))
            {
                boolean hasPerm = false;

                for(String perm : subcmd.getValue().getPermissions())
                    if(sender.hasPermission(perm))
                        hasPerm = true;

                if(hasPerm)
                {
                    subcmd.getValue().execute(sender, tmpArgs.toArray(new String[tmpArgs.size()]));
                    return true;
                }
                else
                {
                    sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "commands.error.perm"));
                }
            }
        }
        return false;
    }

}
