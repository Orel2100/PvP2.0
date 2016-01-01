package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
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
    private String arena;
    private ItemStack[] lastInventory;
    private ItemStack[] lastArmor;
    private Location lastLocation;

    public PvPPlayer(Player p, String _arena, ItemStack[] _lastInv, ItemStack[] _lastArmor, Location _lastLoc)
    {
        // load configuration
        playerConfig = new Config("players/" + p.getUniqueId().toString());
        playerConfig.saveDefaultConfig("playerStats");

        // set values
        this.player = p;
        this.arena = _arena;
        this.lastInventory = _lastInv;
        this.lastArmor = _lastArmor;
        this.lastLocation = _lastLoc;

        // update last known name
        playerConfig.config.set("lastName", p.getName());

        // update last join timestamp
        playerConfig.config.set("lastPvPJoin", System.currentTimeMillis() / 1000);

        this.save();
    }

    // returns the player's language (fallback is the default language)
    public String getLanguage()
    {
        if(playerConfig.config.contains("language"))
            return playerConfig.config.getString("language");
        else
            return PvP.getInstance().getConfig().getString("language");
    }

    // updates player's language
    public void setLanguage(String newLang)
    {
        playerConfig.config.set("language", newLang);
        this.save();
    }

    // adds a kill to the statistics
    public PvPPlayer addKill()
    {
        playerConfig.config.set("stats.kills", playerConfig.config.getInt("stats.kills") + 1);
        this.save();

        return this;
    }

    // adds a death to the statistics
    public PvPPlayer addDeath()
    {
        playerConfig.config.set("stats.deaths", playerConfig.config.getInt("stats.deaths") + 1);
        this.save();

        return this;
    }

    // updates the scoreboard with newest stats
    public PvPPlayer updateScoreboard()
    {
        PvP.getInstance().getLogger().info("Updaing scoreboard for " + player.getName() + "...");
        List<String> scoreboardLines = PvP.getInstance().getConfig().getStringList("ingame.scoreboard.lines");

        Scoreboard scoreboard = PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("pvp", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', PvP.getInstance().getConfig().getString("ingame.scoreboard.title")));

        for(int i = 0; i < scoreboardLines.size(); i++)
        {
            int index = scoreboardLines.size() - i; // the index on the scoreboard
            Score score = objective.getScore(parseScoreboardLine(scoreboardLines.get(i)));
            score.setScore(index);
        }

        player.setScoreboard(scoreboard);

        return this;
    }

    // replaces placeholders with values
    private String parseScoreboardLine(String s)
    {
        // player informations
        String compiled = s.replace("{NAME}", player.getName());
        compiled = compiled.replace("{DISPLAYNAME}", player.getDisplayName());

        // player's location infos
        compiled = compiled.replace("{WORLD}", player.getWorld().getName());

        // pvp stats
        compiled = compiled.replace("{PVPDEATHS}",  Integer.toString(playerConfig.config.getInt("stats.kills")));
        compiled = compiled.replace("{PVPKILLS}",  Integer.toString(playerConfig.config.getInt("stats.deaths")));
        compiled = compiled.replace("{PVPARENA}", InGameManager.instance.getArena(player));

        return ChatColor.translateAlternateColorCodes('&', compiled);
    }

    // resets the statistics for the player
    public PvPPlayer delete()
    {
        playerConfig.saveDefaultConfig("playerStats");
        this.save();

        return this;
    }

    // reloads the configuration
    public void reload()
    {
        playerConfig.reloadConfig();
    }

    // returns the current arena
    public String getArena()
    {
        return this.arena;
    }

    // returns the current inventory
    public Inventory getCurrentInventory()
    {
        return player.getInventory();
    }

    // returns the inventory of the player before he joined,, read-only
    public ItemStack[] getInventoryBeforeJoin()
    {
        return lastInventory;
    }

    // returns the inventory of the player before he joined,, read-only
    public ItemStack[] getArmorBeforeJoin()
    {
        return lastArmor;
    }

    // returns the current location
    public Location getCurrentLocation()
    {
        return player.getLocation();
    }

    // returns the location of the player before he joined, read-only
    public Location getLocationBeforeJoin()
    {
        return lastLocation;
    }
    
    // returns the player-object
    public Player getPlayer()
    {
        return player;
    }

    // changes the current kit in the configuration
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

    public PvPPlayer giveCurrentKit()
    {
        String kit = playerConfig.config.getString("kits.current", "default");

        if(kit.equalsIgnoreCase(""))
            kit = "default";

        if(!PvP.getInstance().getConfig().contains("kits." + kit))
        {
            changeKit("default"); // set kit to default
            this.save();

            return giveCurrentKit();
        }
        else
            giveKit(kit);

        return this;
    }

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

    // saves the configuration, is called in every function
    public void save()
    {
        playerConfig.saveConfig();
    }

}
