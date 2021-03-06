package at.lukasberger.bukkit.pvp.commands.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.commands.AbstractSubCommand;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PlayerKitCommand extends AbstractSubCommand
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
            String kit = args[0];

            if(kit.equalsIgnoreCase("default"))
            {
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.player.default"));
                InGameManager.instance.getPlayer((Player)sender).changeKit("default");
            }
            else
            {
                Set<String> existsingKits = PvP.getInstance().getConfig().getConfigurationSection("keys").getKeys(false);

                if(!existsingKits.contains(kit))
                {
                    sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "action.kit.player.not-existing", kit));
                    return;
                }

                ConfigurationSection kitConfig = PvP.getInstance().getConfig().getConfigurationSection("keys." + kit);
                Player p = (Player)sender;
                double costs = kitConfig.getDouble("costs");
                String currenyName = (costs == 1) ? PvP.economy.currencyNameSingular() : PvP.economy.currencyNamePlural();

                // check if player already owns the kit, do no transaction if so
                boolean transactionSuccessfully = InGameManager.instance.getPlayer((Player)sender).ownsKit(kit);

                if(!transactionSuccessfully && costs < 0) // negative amounts: give player money
                {
                    if(!PvP.economy.hasAccount(p))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get(sender, "action.kit.player.economy.no-account"));
                        transactionSuccessfully = false;
                    }

                    PvP.economy.depositPlayer(p, costs);

                    sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get(sender, "action.kit.player.economy.money-give", costs, currenyName, kit));
                    transactionSuccessfully = true;
                }
                else if(!transactionSuccessfully && costs > 0) // postive amounts: take money from player
                {
                    if(!PvP.economy.hasAccount(p))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get(sender, "action.kit.player.economy.no-account"));
                        transactionSuccessfully = false;
                    }

                    if(!PvP.economy.has(p, costs))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get(sender, "action.kit.player.economy.not-enough-money"));
                        transactionSuccessfully = false;
                    }

                    PvP.economy.withdrawPlayer(p, costs);

                    sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get(sender, "action.kit.player.economy.money-take", costs, currenyName, kit));
                    transactionSuccessfully = true;
                }
                else
                    transactionSuccessfully = true; // amount is 0: do nothing

                if(transactionSuccessfully)
                {
                    InGameManager.instance.getPlayer(p).changeKit(kit);
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get(sender, "action.kit.player.changed", kit));
                }
                else
                {
                    if(costs < 0)
                        sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "action.kit.player.failed-give", kit));
                    else if(costs > 0)
                        sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get(sender, "action.kit.player.failed-take", kit));
                }
            }
        }
        else
        {
            printHelp(sender);
        }
    }

    private void printHelp(CommandSender sender)
    {
        sender.sendMessage(ChatColor.AQUA + "~~~ PvP-Admin: Kits ~~~");
        sender.sendMessage(ChatColor.GRAY + "/pvp kit/k {Name}\n" + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.buy"));
    }

    @Override
    public List<String> getHelp(CommandSender sender)
    {
        return Arrays.asList(
                ChatColor.GRAY + "/pvp kit {Name}\n" +
                        "    \u00BB\u00BB " + ChatColor.GREEN + MessageManager.instance.get(sender, "commands.help.kit.buy")
        );
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.kit", "pvp.player.*", "pvp.*");
    }

}