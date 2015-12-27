package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.core.objects.Arena;
import at.lukasberger.bukkit.pvp.core.objects.PvPPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PlayerManager
{

    // lists
    private static HashMap<String, PvPPlayer> players = new HashMap<>();

    // instance
    public static PlayerManager instance = new PlayerManager();

    // disallow creation of other instances
    private PlayerManager() { }

    // loads the player into the list
    public void loadPlayer(Player p, String arena, ItemStack[] lastInv, ItemStack[] lastArmor, Location lastLoc)
    {
        players.put(p.getUniqueId().toString(), new PvPPlayer(p, arena, lastInv, lastArmor, lastLoc));
    }

    // returns the player-object
    public PvPPlayer getPlayer(String pl)
    {
        return players.get(pl);
    }

    // returns the player-object
    public PvPPlayer getPlayer(Player pl)
    {
        return getPlayer(pl.getUniqueId().toString());
    }

    // unloads the player from the list
    public void unloadPlayer(String pl)
    {
        players.remove(pl);
    }

    // removes all players from list
    public void unloadAllPlayers()
    {
        for(String key : players.keySet())
            players.remove(key);
    }

    // completely deletes the player(-statistiks)
    public void deletePlayer(String pl)
    {
        PvPPlayer a = players.get(pl);
        a.delete();
        players.remove(pl);
    }

    public boolean isPlayerLoaded(String pl)
    {
        return players.containsKey(pl);
    }

}
