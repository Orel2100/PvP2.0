package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.messages.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class ArenaCommand extends AbstractSubCommand
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
            if(args[1].equalsIgnoreCase("create"))
            {
                sender.sendMessage(PvP.prefix + MessageManager.instance.get("action.arena.create"));
                Selection sel = PvP.worldEdit.getSelection((Player)sender);
                Arena.createArena(sel, args[0]);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.arena.created", args[0]));

                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get("action.arena.created-help-addspawn", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get("action.arena.created-help-delspawn", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get("action.arena.created-help-randspawn"));
            }
            else if(args[1].equalsIgnoreCase("delete"))
            {
                new Arena(args[0]).delete();
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.arena.deleted", args[0]));
            }
            else if(args[1].equalsIgnoreCase("addspawn"))
            {
                Location loc = ((Player)sender).getLocation();
                int id = new Arena(args[0]).addSpawn(loc);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.arena.spawn.added", id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
            else if(args[1].equalsIgnoreCase("delspawn"))
            {
                int id = new Arena(args[0]).removeLastSpawn();
                if(id == -1)
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.arena.spawn.no-spawn-delete", id));
                else
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.arena.spawn.deleted", id));
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
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP-Admin: Arena ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} create\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.arena.create"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} delete\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.arena.delete"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} addspawn\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.arena.addspawn"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} delspawn\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.arena.delspawn"));
}

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.arena", "pvp.admin", "pvp.*");
    }

}