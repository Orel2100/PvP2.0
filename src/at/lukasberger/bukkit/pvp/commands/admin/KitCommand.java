package at.lukasberger.bukkit.pvp.commands.admin;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class KitCommand extends AbstractSubCommand
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
                if(PvP.getInstance().getConfig().contains("kits." + args[0]))
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.already-existing", args[0]));
                    return;
                }

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.create", args[0]));

                PvP.getInstance().getConfig().set("kits." + args[0], null);
                PvP.getInstance().getConfig().set("kits." + args[0] + ".costs", 0);
                PvP.getInstance().getConfig().set("kits." + args[0] + ".item", Arrays.asList(""));

                PvP.getInstance().saveConfig();

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.created", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.kit.created-help-additem", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.kit.created-help-delitem", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.kit.created-help-setcosts", args[0]));
                sender.sendMessage(ChatColor.AQUA + MessageManager.instance.get(sender, "action.kit.created-help-delete", args[0]));
            }
            else if(args[1].equalsIgnoreCase("delete"))
            {
                if(!PvP.getInstance().getConfig().contains("kits." + args[0]))
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.not-existing", args[0]));
                    return;
                }

                PvP.getInstance().getConfig().set("kits." + args[0], null);
                PvP.getInstance().saveConfig();

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.deleted", args[0]));
            }
            else
            {
                printHelp(sender);
            }
        }
        else if(args.length == 3)
        {
            if(args[1].equalsIgnoreCase("costs"))
            {
                if(!PvP.getInstance().getConfig().contains("kits." + args[0]))
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.not-existing", args[0]));
                    return;
                }

                PvP.getInstance().getConfig().set("kits." + args[0] + ".costs", Integer.parseInt(args[2]));
                PvP.getInstance().saveConfig();

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.cost-changed", args[0], args[2]));
            }
            else if(args[1].equalsIgnoreCase("add"))
            {
                if(!PvP.getInstance().getConfig().contains("kits." + args[0]))
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.not-existing", args[0]));
                    return;
                }

                String slot = args[2];
                List<String> items = PvP.getInstance().getConfig().getStringList("kits." + args[0] + ".items");
                boolean slotsUsed = false;

                for(String item : items)
                    if(item.startsWith(slot + ";"))
                        slotsUsed = true;

                if(slotsUsed)
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.slot-used", args[2], args[0]));
                    return;
                }

                ItemStack selectedItem = ((Player)sender).getItemInHand();

                String serializedItem = "";
                String serializedEnchantments = "";

                serializedItem += slot + ";";
                serializedItem += selectedItem.getType().name().toUpperCase() + ";";
                serializedItem += selectedItem.getAmount() + ";";

                if(selectedItem.getItemMeta() != null && selectedItem.getItemMeta().getDisplayName() != null)
                    serializedItem += selectedItem.getItemMeta().getDisplayName().replace(ChatColor.COLOR_CHAR, '&') + ";";
                else
                    serializedItem += ";";

                for(Map.Entry<Enchantment, Integer> ench : selectedItem.getEnchantments().entrySet())
                    serializedEnchantments += ench.getKey().getName().toUpperCase() + ":" + ench.getValue() + ",";

                serializedEnchantments = serializedEnchantments.substring(0, serializedEnchantments.length() - 1);

                serializedItem += serializedEnchantments;

                items.add(serializedItem);

                PvP.getInstance().getConfig().set("kits." + args[0] + ".items", items);
                PvP.getInstance().saveConfig();

                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.item-added", args[0], args[2]));
            }
            else if(args[1].equalsIgnoreCase("del"))
            {
                if(!PvP.getInstance().getConfig().contains("kits." + args[0]))
                {
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.not-existing", args[0]));
                    return;
                }

                String slot = args[2];
                List<String> items = PvP.getInstance().getConfig().getStringList("kits." + args[0] + ".items");
                boolean slotDeleted = false;

                for(int i = 0; i < items.size(); i++)
                {
                    String item = items.get(i);

                    if(item.startsWith(slot + ";"))
                    {
                        items.remove(i);
                        slotDeleted = true;
                        break;
                    }
                }

                PvP.getInstance().getConfig().set("kits." + args[0] + ".items", items);
                PvP.getInstance().saveConfig();

                if(slotDeleted)
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.item-deleted", args[2], args[0]));
                else
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.slot-not-used", args[2], args[0]));
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
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP-Admin: Kits-Admin ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp kita {Name} create\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.create"));
        sender.sendMessage(ChatColor.GRAY + "/pvp kita {Name} add\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.additem"));
        sender.sendMessage(ChatColor.GRAY + "/pvp kita {Name} del\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.delitem"));
        sender.sendMessage(ChatColor.GRAY + "/pvp kita {Name} delete\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.delete"));
        sender.sendMessage(ChatColor.GRAY + "/pvp kita {Name} costs\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.costs"));
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.admin.kit", "pvp.admin", "pvp.*");
    }

}

