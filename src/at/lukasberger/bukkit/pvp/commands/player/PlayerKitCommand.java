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
 * Created by Lukas on 30.12.2015.
 */
public class PlayerKitCommand extends AbstractSubCommand
{

    @Override
    public void execute(CommandSender sender, String[] args)
    {
        if(args.length == 0)
        {
            printHelp(sender);
        }
        else if(args.length == 1)
        {
            String kit = args[0];

            if(kit.equalsIgnoreCase("default"))
            {
                sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.kit.player.default"));
                InGameManager.instance.getPlayer((Player)sender).changeKit("default");
            }
            else
            {
                Set<String> existsingKits = PvP.getInstance().getConfig().getConfigurationSection("keys").getKeys(false);

                if(!existsingKits.contains(kit))
                {
                    sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.kit.player.not-existing", kit));
                    return;
                }

                ConfigurationSection kitConfig = PvP.getInstance().getConfig().getConfigurationSection("keys." + kit);
                Player p = (Player)sender;
                double costs = kitConfig.getDouble("costs");
                String currenyName = (costs == 1) ? PvP.economy.currencyNameSingular() : PvP.economy.currencyNamePlural();

                boolean transactionSuccessfully = false;

                if(costs < 0) // negative amounts: give player money
                {
                    if(!PvP.economy.hasAccount(p))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.kit.player.economy.no-account"));
                        transactionSuccessfully = false;
                    }

                    PvP.economy.depositPlayer(p, costs);

                    sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.kit.player.economy.money-give", costs, currenyName, kit));
                    transactionSuccessfully = true;
                }
                else if(costs > 0) // postive amounts: take money from player
                {
                    if(!PvP.economy.hasAccount(p))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.kit.player.economy.no-account"));
                        transactionSuccessfully = false;
                    }

                    if(!PvP.economy.has(p, costs))
                    {
                        sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.kit.player.economy.not-enough-money"));
                        transactionSuccessfully = false;
                    }

                    PvP.economy.withdrawPlayer(p, costs);

                    sender.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.kit.player.economy.money-take", costs, currenyName, kit));
                    transactionSuccessfully = true;
                }
                else
                    transactionSuccessfully = true; // amount is 0: do nothing

                if(transactionSuccessfully)
                {
                    InGameManager.instance.getPlayer(p).changeKit(kit);
                    sender.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.kit.player.changed", kit));
                }
                else
                {
                    if(costs < 0)
                        sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.kit.player.failed-give", kit));
                    else if(costs > 0)
                        sender.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.kit.player.failed-take", kit));
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
        sender.sendMessage(ChatColor.GRAY + "/pvp kit {Name}\n" + ChatColor.GREEN + MessageManager.instance.get("commands.help.kit.buy"));
    }

    @Override
    public List<String> getPermissions()
    {
        return Arrays.asList("pvp.player.kit", "pvp.player", "pvp.player.*", "pvp.*");
    }

}