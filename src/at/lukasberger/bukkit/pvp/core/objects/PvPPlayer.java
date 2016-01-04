package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.AfkManager;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.utils.MathUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayer
{

    // configuration
    private Config playerConfig;

    // values
    private Player player;

    public PvPPlayer(Player p)
    {
        // load configuration
        playerConfig = new Config("players/" + p.getUniqueId().toString());
        playerConfig.saveDefaultConfig("player");

        // set variables
        this.player = p;

        // update last known name
        playerConfig.config.set("lastName", p.getName());

        // update last join timestamp
        playerConfig.config.set("lastSeen", System.currentTimeMillis() / 1000);

        if(!playerConfig.config.contains("language"))
            playerConfig.config.set("language", PvP.getInstance().getConfig().getString("language"));
        if(!playerConfig.config.contains("kit.current"))
            playerConfig.config.set("kit.current", "default");

        if(!playerConfig.config.contains("stats.kills"))
            playerConfig.config.set("stats.kills", 0);
        if(!playerConfig.config.contains("stats.deaths"))
            playerConfig.config.set("stats.deaths", 0);
        if(!playerConfig.config.contains("stats.elo"))
            playerConfig.config.set("stats.elo", 1500);

        this.save();
    }

    /**
     * Returns the own language of the player
     * @return The personal language of the player
     */
    public String getLanguage()
    {
        if(playerConfig.config.contains("language"))
            return playerConfig.config.getString("language");
        else
            return PvP.getInstance().getConfig().getString("language");
    }

    /**
     * Changes player's own language
     * @return This instance
     */
    public PvPPlayer setLanguage(String newLang)
    {
        playerConfig.config.set("language", newLang);
        this.save();

        return this;
    }

    /**
     * Add a kill to player's stats
     * @return This instance
     */
    public PvPPlayer addKill()
    {
        playerConfig.config.set("stats.kills", playerConfig.config.getInt("stats.kills") + 1);
        this.save();

        return this;
    }

    /**
     * Add a death to player's stats
     * @return This instance
     */
    public PvPPlayer addDeath()
    {
        playerConfig.config.set("stats.deaths", playerConfig.config.getInt("stats.deaths") + 1);
        this.save();

        return this;
    }

    /**
     * Updtes the player's ELO
     * @return This instance
     */
    public PvPPlayer updateElo(int enemyElo, boolean winner)
    {
        Integer personalElo = this.getElo();

        double expectElo = MathUtils.round(1.0 / (1.0 + Math.pow(10.0, (enemyElo - personalElo) / 400.0)), 3);
        double newElo = MathUtils.round(personalElo + (20.0 * ((winner ? 1.0 : 0.0) - expectElo)), 0);

        this.playerConfig.config.set("stats.elo", (int)newElo);
        this.save();
        return this;
    }

    /**
     * Return the player's ELO
     * @return This instance
     */
    public Integer getElo()
    {
        return playerConfig.config.getInt("stats.elo", Integer.MIN_VALUE);
    }

    /**
     * Updates the scoreboard of the player
     * @return This instance
     */
    public PvPPlayer updateScoreboard()
    {
        return updateScoreboard(player.getLocation());
    }

    /**
     * Updates the scoreboard of the player
     * @param loc The current location of the player
     * @return This instance
     */
    public PvPPlayer updateScoreboard(Location loc)
    {
        List<String> scoreboardLines = PvP.getInstance().getConfig().getStringList("ingame.scoreboard.lines");
        Scoreboard scoreboard;
        Objective objective;

        scoreboard = PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        objective = scoreboard.registerNewObjective("pvp", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', PvP.getInstance().getConfig().getString("ingame.scoreboard.title")));

        for(int i = 0; i < scoreboardLines.size(); i++)
        {
            int index = scoreboardLines.size() - i; // the index on the scoreboard
            String indexAppend = "";

            for(int j = 0; j < index; j++)
                indexAppend += ChatColor.RESET.toString();

            Score score = objective.getScore(parseScoreboardLine(scoreboardLines.get(i), loc) + indexAppend);
            score.setScore(index);
        }

        player.setScoreboard(scoreboard);

        return this;
    }

    // replaces placeholders with values
    private String parseScoreboardLine(String s, Location loc)
    {
        // player informations
        String compiled = s.replace("{name}", player.getName());
        compiled = compiled.replace("{display_name}", player.getDisplayName());

        // player's location infos
        compiled = compiled.replace("{world}", player.getWorld().getName());
        compiled = compiled.replace("{loc_x}", Integer.toString(loc.getBlockX()));
        compiled = compiled.replace("{loc_y}", Integer.toString(loc.getBlockY()));
        compiled = compiled.replace("{loc_z}", Integer.toString(loc.getBlockZ()));

        // pvp stats
        compiled = compiled.replace("{pvp_deaths}",  Integer.toString(playerConfig.config.getInt("stats.deaths")));
        compiled = compiled.replace("{pvp_kills}",  Integer.toString(playerConfig.config.getInt("stats.kills")));
        compiled = compiled.replace("{pvp_elo}",  Integer.toString(playerConfig.config.getInt("stats.elo")));

        // current pvp-settings
        compiled = compiled.replace("{pvp_arena}", InGameManager.instance.getArena(player).getName());
        compiled = compiled.replace("{pvp_lang}", InGameManager.instance.getPlayer(player).getLanguage());
        compiled = compiled.replace("{pvp_kit}", playerConfig.config.getString("kits.current", "Default"));
        compiled = compiled.replace("{pvp_afk}", AfkManager.instance.isPlayerAfk(player) ? ChatColor.RED + "\u2B24 AFK" : ChatColor.GREEN + "\u2B24 In-Game");

        return ChatColor.translateAlternateColorCodes('&', compiled);
    }

    /**
     * Overwrites player's statistics, configurations etc.
     * @return This instance
     */
    public PvPPlayer delete()
    {
        playerConfig.saveDefaultConfig("playerStats", true);
        this.save();

        return this;
    }

    /**
     * Reloads the player's configuration from disk
     * @return This instance
     */
    public PvPPlayer reload()
    {
        playerConfig.reloadConfig();
        return this;
    }

    /**
     * Returns the current arena of the player
     * @return Arena-object of player's current arena
     */
    public Arena getArena()
    {
        return InGameManager.instance.getArena(player);
    }

    /**
     * Returns the player-object of Bukkit
     * @return Bukkit-Player
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Indicates if player already owns the kit
     * @param kit Name of the kit
     * @return If player owns the kit or not
     */
    public boolean ownsKit(String kit)
    {
        return playerConfig.config.getStringList("kits.bought").contains(kit);
    }

    /**
     * Changes player's current kit in configuration and add it to list of
     * bought kits if it's not there already
     * @param newKit Name of the kit
     * @return This instance
     */
    public PvPPlayer changeKit(String newKit)
    {
        playerConfig.config.set("kits.current", newKit);

        List<String> boughtKits = playerConfig.config.getStringList("kits.bought");

        if(!boughtKits.contains(playerConfig.config.getString("kits.current")))
            boughtKits.add(playerConfig.config.getString("kits.current"));

        playerConfig.config.set("kits.bought", boughtKits);

        this.save();
        return this;
    }

    /**
     * Give's the current kit to the player
     * @return This instance
     */
    public PvPPlayer giveCurrentKit()
    {
        String kit = playerConfig.config.getString("kits.current", "default");

        if(kit.equalsIgnoreCase(""))
        {
            changeKit("default"); // set kit to default
            return giveCurrentKit();
        }

        if(!PvP.getInstance().getConfig().contains("kits." + kit))
        {
            changeKit("default"); // set kit to default
            return giveCurrentKit();
        }
        else
            giveKit(kit);

        return this;
    }

    /**
     * Give's the kit with the defined name to the player
     * @return This instance
     */
    public PvPPlayer giveKit(String kit)
    {
        List<String> rawItems = PvP.getInstance().getConfig().getStringList("kits." + kit + ".items");
        int index = 0;

        for(String rawItem : rawItems)
        {
            String[] parts = rawItem.split(";");

            if(parts.length < 2)
            {
                PvP.getInstance().getLogger().severe("[Kits] The item #" + (index + 1) + " of the kit " + kit + " is wrong configured. You should at least give the slot number and the item name/id.");
                continue;
            }

            int slot = -1;
            String itemName = "";
            int itemAmount = -1;
            String customName = "";
            List<String> enchantments = new ArrayList<>();

            for(int i = 0; i < parts.length; i++)
            {
                switch(i)
                {
                    case 0: // slot-index
                        if(parts[i].equalsIgnoreCase("helmet"))
                            slot = -2;
                        else if(parts[i].equalsIgnoreCase("plate"))
                            slot = -3;
                        else if(parts[i].equalsIgnoreCase("leggings"))
                            slot = -4;
                        else if(parts[i].equalsIgnoreCase("boots"))
                            slot = -5;
                        else
                            slot = Integer.parseInt(parts[i]);

                        break;

                    case 1: // item name/id
                        itemName = parts[i];
                        break;

                    case 2: // amount of item
                        itemAmount = Integer.parseInt(parts[i]);
                        break;

                    case 3: // display name of item
                        customName = parts[i];
                        break;

                    case 4: // display name of item
                        enchantments.addAll(Arrays.asList(parts[i].split(",")));
                        break;

                    default:
                        break;
                }
            }

            ItemStack item = new ItemStack(Material.getMaterial(itemName.toUpperCase()), itemAmount);

            ItemMeta item_meta = item.getItemMeta();
            item_meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', customName));
            item.setItemMeta(item_meta);

            for(String ench : enchantments)
                item.addUnsafeEnchantment(Enchantment.getByName(ench.split(":")[0].toUpperCase()), Integer.parseInt(ench.split(":")[1]));

            if(slot == -2)
                player.getInventory().setHelmet(item);
            else if(slot == -3)
                player.getInventory().setChestplate(item);
            else if(slot == -4)
                player.getInventory().setLeggings(item);
            else if(slot == -5)
                player.getInventory().setBoots(item);
            else
                player.getInventory().setItem(slot, item);

            index++;
        }

        return this;
    }

    /**
     * Saves the configuration of the player
     * @return This instance
     */
    public PvPPlayer save()
    {
        playerConfig.saveConfig();
        return this;
    }

}
