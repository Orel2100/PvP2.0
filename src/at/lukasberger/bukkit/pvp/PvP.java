package at.lukasberger.bukkit.pvp;

import at.lukasberger.bukkit.pvp.commands.SubCommandManager;
import at.lukasberger.bukkit.pvp.commands.admin.*;
import at.lukasberger.bukkit.pvp.commands.player.*;
import at.lukasberger.bukkit.pvp.core.*;
import at.lukasberger.bukkit.pvp.core.objects.Config;
import at.lukasberger.bukkit.pvp.events.inventory.*;
import at.lukasberger.bukkit.pvp.events.player.*;
import at.lukasberger.bukkit.pvp.events.player.afk.*;
import at.lukasberger.bukkit.pvp.events.player.items.*;
import at.lukasberger.bukkit.pvp.events.player.party.*;
import at.lukasberger.bukkit.pvp.events.player.spectator.*;
import at.lukasberger.bukkit.pvp.events.world.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Set;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvP extends JavaPlugin
{

    // Chat-Prefixes for PvP
    private static String basePrefix = ChatColor.GRAY + "[" + ChatColor.AQUA + ChatColor.BOLD + "PvP" + ChatColor.RESET + ChatColor.GRAY + "]";
    public static String prefix = basePrefix + ChatColor.RESET + " ";
    public static String errorPrefix = basePrefix + ChatColor.RED + " ";
    public static String warningPrefix = basePrefix + ChatColor.YELLOW + " ";
    public static String successPrefix = basePrefix + ChatColor.GREEN + " ";

    // inventory-help
    public static String inventoryHelpTitle = ChatColor.AQUA + "PvP 2.0" + ChatColor.GRAY + " - " + ChatColor.WHITE + "The new PvP-Plugin";
    public static Inventory inventoryHelp = null;

    // Load instance of PvP
    public static PvP getInstance()
    {
        return (PvP)Bukkit.getPluginManager().getPlugin("PvP");
    }

    // Instances from dependencies
    public static WorldEditPlugin worldEdit = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
    public static Economy economy = null;

    public static boolean isDisabling = false;

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

        // they need to be fix
        this.getConfig().set("gadgets.grenades.item", "EGG");
        this.saveConfig();

        // vault
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
            economy = economyProvider.getProvider();

        if(economy == null)
        {
            this.getServer().getLogger().severe("FAILED TO LOAD VAULT-ECONOMY, DISABLING PLUGIN...!");
            this.getPluginLoader().disablePlugin(this);
            return;
        }

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
        this.loadSubCommands();

        this.getLogger().info("Registering events...");
        this.registerEvents();

        if(this.getConfig().getBoolean("ingame.enable-afk"))
        {
            this.getLogger().info("Starting synchronous AFK-Task...");
            AfkManager.instance.startTasks();
        }

        this.getLogger().info("Finished loading of PvP2.0 v" + getDescription().getVersion() + ", waiting for loading languages...");

        this.getServer().getScheduler().runTaskLater(this, new Runnable() {

            @Override
            public void run()
            {
                getLogger().info("Saving included languages...");
                new Config("langs/de").saveDefaultConfig("lang_de");
                new Config("langs/en").saveDefaultConfig("lang");

                getLogger().info("Loading default language...");
                MessageManager.instance.loadLanguage(getConfig().getString("language"));

                getLogger().info("Language loaded!");
            }

        }, 0L);
    }

    public void loadSubCommands()
    {
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

        if(this.getConfig().getBoolean("ingame.enable-spectating"))
            SubCommandManager.instance.registerSubCommand(new SpectateCommand(), "spec", "s");

        if(this.getConfig().getBoolean("player-language"))
            SubCommandManager.instance.registerSubCommand(new PlayerLanguageCommand(), "lang", "language");

        SubCommandManager.instance.registerSubCommand(new PlayerKitCommand(), "kit", "k");
        SubCommandManager.instance.registerSubCommand(new EloCommand(), "elo");

        // Admins-Commands
        SubCommandManager.instance.registerSubCommand(new ArenaCommand(), "arena");
        SubCommandManager.instance.registerSubCommand(new KitCommand(), "kita");
        SubCommandManager.instance.registerSubCommand(new LanguageCommand(), "langa", "languagea");
        SubCommandManager.instance.registerSubCommand(new ReloadCommand(), "reload");
        SubCommandManager.instance.registerSubCommand(new FullReloadCommand(), "fullreload");
    }

    public void registerEvents()
    {
        // inventory
        this.getServer().getPluginManager().registerEvents(new PvPInventoryDragEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPInventoryHelpEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PvPInventoryMoveItemEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPInventoryOpenEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPItemDropEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPItemPickupEvent(), this);

        // player
        this.getServer().getPluginManager().registerEvents(new PvPPlayerCommandPreprocessEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerDeathEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerFallDamage(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerGameModeChangeEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerMoveEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerQuitEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerTeleportEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerToggleFlightEvent(), this);
        // party
        this.getServer().getPluginManager().registerEvents(new PvPPartyPlayerQuitEvent(), this);
        // items
        this.getServer().getPluginManager().registerEvents(new PvPPlayerGrenadeEvents(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerBowEvent(), this);
        // afk
        this.getServer().getPluginManager().registerEvents(new PvPPlayerAfkChatEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerAfkDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerAfkInteractEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPPlayerAfkMoveEvent(), this);
        // spectating
        this.getServer().getPluginManager().registerEvents(new PvPSpectatorDamageEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPSpectatorGameModeChangeEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPSpectatorMoveEvent(), this);

        // world
        this.getServer().getPluginManager().registerEvents(new PvPBlockBreakEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PvPBlockPlaceEvent(), this);
    }

    @Override
    public void onDisable()
    {
        isDisabling = true;
        inventoryHelp = null;

        PvP.getInstance().getLogger().warning("Kicking all players from arena...");
        InGameManager.instance.leaveArenaAll();

        PvP.getInstance().getLogger().warning("Unloading loaded arenas...");
        ArenaManager.instance.unloadAllArenas();

        PvP.getInstance().getLogger().warning("Unloading loaded players...");
        PlayerManager.instance.unloadAllPlayers();

        PvP.getInstance().getLogger().warning("Stopping AFK-Task...");
        AfkManager.instance.stopTasks();

        PvP.getInstance().getLogger().warning("Removing invites...");
        InviteManager.instance.removeAll();

        PvP.getInstance().getLogger().warning("Removing parties...");
        PartyManager.instance.removeAll();

        PvP.getInstance().getLogger().warning("Unregister subcommands...");
        SubCommandManager.instance.unregisterAllSubCommands();

        PvP.getInstance().getLogger().warning("Unregister events...");
        HandlerList.unregisterAll(PvP.getInstance());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if(cmd.getName().equalsIgnoreCase("pvp"))
        {
            if(args.length == 0)
            {
                // DO NOT REMOVE THIS COPYRIGHT
                sender.sendMessage(ChatColor.AQUA.toString() + ChatColor.AQUA + "PvP 2.0 v" + getDescription().getVersion() + " - The new PvP-Plugin by Lukas0610");
            }
            else
            {
                if(!SubCommandManager.instance.executeSubCommand(sender, args))
                    sender.sendMessage(errorPrefix + MessageManager.instance.get(sender, "commands.error.subcommand"));
            }
            return true;
        }
        return false;
    }

}
