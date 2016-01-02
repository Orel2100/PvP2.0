package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.core.objects.PvPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PlayerManager
{

    // lists
    private HashMap<String, PvPPlayer> players = new HashMap<>();

    // instance
    public static PlayerManager instance = new PlayerManager();

    // disallow creation of other instances
    private PlayerManager() { }

    /**
     * Loads the player with PvP-Informations into memory
     * @param p The Player
     */
    public void loadPlayer(Player p)
    {
        players.put(p.getUniqueId().toString(), new PvPPlayer(p));
    }


    /**
     * Returns the PvPPlayer-object using the player's UUID
     * @param uuid The UUID of the player
     * @return The PvPPlayer-Object
     */
    public PvPPlayer getPlayer(String uuid)
    {
        return players.get(uuid);
    }

    /**
     * Returns the PvPPlayer-object
     * @param pl The player
     * @return The PvPPlayer-Object
     */
    public PvPPlayer getPlayer(Player pl)
    {
        return getPlayer(pl.getUniqueId().toString());
    }

    /**
     * Removes the player from internal lists using the player's UUID
     * @param uuid The UUID of the player
     */
    public void unloadPlayer(String uuid)
    {
        players.remove(uuid);
    }

    /**
     * Removes all players from internal lists
     */
    public void unloadAllPlayers()
    {
        for(String key : players.keySet())
        {
            getPlayer(key).save();
            players.remove(key);
        }
    }

    /**
     * Completley deletes player's statistics, configurations etc.
     * @param pl The Player
     */
    public void deletePlayer(String pl)
    {
        PvPPlayer a = players.get(pl);
        a.delete();
        players.remove(pl);
    }

    /**
     * Indicates if the player was loaded to internal lists
     * @param pl The Player
     * @return If the player was loaded to lists or not
     */
    public boolean isPlayerLoaded(String pl)
    {
        return players.containsKey(pl);
    }

}
