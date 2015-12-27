package at.lukasberger.bukkit.pvp.core.objects;

import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class Party
{

    // lists
    private static HashMap<String, PvPPlayer> players = new HashMap<>();

    public static void createParty(Player admin)
    {

    }

    // checks if the given player is in this party
    public boolean isPlayerInParty(String pl)
    {
        return players.containsKey(pl);
    }

    // checks if the given player is in this party
    public boolean isPlayerInParty(Player p)
    {
        return isPlayerInParty(p.getUniqueId().toString());
    }

}
