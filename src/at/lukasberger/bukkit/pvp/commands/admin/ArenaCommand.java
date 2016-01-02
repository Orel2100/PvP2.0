package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class ArenaCommand extends AbstractSubCommand
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
        else if(args.length == 2)
        {
            if(args[1].equalsIgnoreCase("create"))
            {
                sender.sendMessage(PvP.prefix + MessageManager.instance.get(sender, "action.arena.create"));
                Selection sel = PvP.worldEdit.getSelection((Player)sender);
                Arena.createArena(sel, args[0]);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.created", args[0]));

                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.arena.created-help-addspawn", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.arena.created-help-delspawn", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.arena.created-help-randspawn"));
            }
            else if(args[1].equalsIgnoreCase("change"))
            {
                Selection sel = PvP.worldEdit.getSelection((Player)sender);
                ArenaManager.instance.getArena(args[0]).changeSelection(sel);

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.changed", args[0]));
            }
            else if(args[1].equalsIgnoreCase("delete"))
            {
                ArenaManager.instance.deleteArena(args[0]);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.deleted", args[0]));
            }
            else if(args[1].equalsIgnoreCase("addspawn"))
            {
                Location loc = ((Player)sender).getLocation();
                int id = ArenaManager.instance.getArena(args[0]).addSpawn(loc);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.spawn.added", id, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
            }
            else if(args[1].equalsIgnoreCase("delspawn"))
            {
                int id = ArenaManager.instance.getArena(args[0]).removeLastSpawn();
                if(id == -1)
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.spawn.no-spawn-delete", id));
                else
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.spawn.deleted", id));
            }
            else if(args[1].equalsIgnoreCase("spec"))
            {
                Location loc = ((Player)sender).getLocation();
                ArenaManager.instance.getArena(args[0]).setSpecSpawn(loc);
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.arena.spec-set", loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()));
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
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} create\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.arena.create"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} delete\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.arena.delete"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} addspawn\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.arena.addspawn"));
        sender.sendMessage(ChatColor.GRAY + "/pvp arena {Name} delspawn\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.arena.delspawn"));
}

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.arena", "pvp.admin", "pvp.*");
    }

}
