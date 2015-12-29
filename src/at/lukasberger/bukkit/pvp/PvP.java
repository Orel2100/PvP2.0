package at.lukasberger.bukkit.pvp;

import at.lukasberger.bukkit.pvp.commands.SubCommandManager;
import at.lukasberger.bukkit.pvp.commands.admin.*;
import at.lukasberger.bukkit.pvp.commands.player.*;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import at.lukasberger.bukkit.pvp.events.inventory.*;
import at.lukasberger.bukkit.pvp.events.player.*;
import at.lukasberger.bukkit.pvp.events.player.items.*;
import at.lukasberger.bukkit.pvp.events.player.party.*;
import at.lukasberger.bukkit.pvp.events.world.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Set;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvP extends JavaPlugin
{

    // Chat-Prefixes for PvP
    private static String basePrefix = ChatColor.GRAY + "[" + ChatColor.AQUA + ChatColor.BOLD + "PvP" + ChatColor.RESET + ChatColor.GRAY + "]";
    public static String prefix = basePrefix + ChatColor.RESET + " ";
    public static String errorPrefix = basePrefix + ChatColor.RED + " ";
    public static String warningPrefix = basePrefix + ChatColor.YELLOW + " ";
    public static String successPrefix = basePrefix + ChatColor.GREEN + " ";

    // Load instance of PvP
    public static PvP getInstance()
    {
        return (PvP)Bukkit.getPluginManager().getPlugin("PvP");
    }

    // Instances from dependencies
    public static WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");

    @Override
    public void onEnable()
    {
        this.getLogger().info("Saving default files...");
        this.saveDefaultConfig();
        this.reloadConfig();

        this.getLogger().info("Updating configs...");
        Config currentConfig = new Config("conf-def");
        currentConfig.saveDefaultConfig("config", true);

        Set<String> keys = currentConfig.config.getKeys(true);
        for(String key : keys)
        {
            if (!this.getConfig().getKeys(true).contains(key))
            {
                this.getLogger().info("[Configuration] Updating \"" + key + "\" to value \"" + currentConfig.config.get(key).toString() + "\"");
                this.getConfig().set(key, currentConfig.config.get(key));
            }
        }

        currentConfig.delete();

        // they need to be fixed
        this.getConfig().set("gadgets.grenades.item", "EGG");
        this.getConfig().set("gadgets.machinegun.item", "SNOWBALL");

        this.saveConfig();

        if(!getConfig().contains("kits.default"))
        {
            PvP.getInstance().getLogger().warning("[Kits] Adding default kit");

            PvP.getInstance().getConfig().set("kits.default.cost", 0);
            PvP.getInstance().getConfig().set("kits.default.items",
                    Arrays.asList(
                            "0;diamond_sword;1;&bPvP&7-Sword;sharpness:2",
                            "1;261;1;&bPvP&7-Bow;infinity:1,durability:1",
                            "2;fishing_rod;1;;durability:1",
                            "4;golden_apple;3",
                            "9;arrow"
                    ));
            PvP.getInstance().saveConfig();
        }

        this.getLogger().info("Loading sub-commands...");

        // Player-Commands
        SubCommandManager.instance.registerSubCommand(new JoinCommand(), "join", "j");
        SubCommandManager.instance.registerSubCommand(new LeaveCommand(), "leave", "l");

        if(this.getConfig().getBoolean("ingame.enable-invites"))
        {
            SubCommandManager.instance.registerSubCommand(new InviteCommand(), "invite", "i");
            SubCommandManager.instance.registerSubCommand(new InviteAcceptCommand(), "accept", "a");
            SubCommandManager.instance.registerSubCommand(new InviteDenyCommand(), "deny", "d");
        }

        if(this.getConfig().getBoolean("ingame.enable-parties"))
            SubCommandManager.instance.registerSubCommand(new PartyCommand(), "party", "p");

        // Admins-Commands
        SubCommandManager.instance.registerSubCommand(new ArenaCommand(), "arena");
        SubCommandManager.instance.registerSubCommand(new KitCommand(), "kit");
        SubCommandManager.instance.registerSubCommand(new LanguageCommand(), "lang", "language");

        this.getLogger().info("Registering events...");

        // inventory
        this.getServer().getPluginManager().registerEvents(new PvPInventoryDragEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPInventoryMoveItemEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPInventoryOpenEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPItemDropEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPItemPickupEvent(), this);

        // player
        this.getServer().getPluginManager().registerEvents(new PvPPlayerDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerDeathEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerFallDamage(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerGameModeChangeEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerMoveEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerTeleportEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerToggleFlightEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerQuitEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPartyPlayerQuitEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerGrenadeEvents(), this);

        // world
        this.getServer().getPluginManager().registerEvents(new PvPBlockBreakEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPBlockPlaceEvent(), this);

        this.getLogger().info("Finished loading of PvP2.0 v" + getDescription().getVersion() + ", waiting for loading languages...");

        this.getServer().getScheduler().runTaskLater(this, new Runnable() {

            @Override
            public void run()
            {
                getLogger().info("Loading language...");
                MessageManager.instance.loadLanguage(getConfig().getString("language"));
                getLogger().info("Language loaded!");
            }

        }, 0L);
    }

    @Override
    public void onDisable()
    {
        SubCommandManager.instance.unregisterAllSubCommands();
        InGameManager.instance.leaveArenaAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("pvp"))
        {
            if(args.length == 0)
            {
                sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.AQUA + "PvP 2.0 v" + getDescription().getVersion() + " - The new PvP-Plugin");
            }
            else
            {
                if(!SubCommandManager.instance.executeSubCommand(sender, args))
                {
                    sender.sendMessage(errorPrefix + MessageManager.instance.get("commands.error.subcommand"));
                }
            }
            return true;
        }
        return false;
    }

}
