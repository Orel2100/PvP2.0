package at.lukasberger.bukkit.pvp.commands;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.MessageManager;
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
