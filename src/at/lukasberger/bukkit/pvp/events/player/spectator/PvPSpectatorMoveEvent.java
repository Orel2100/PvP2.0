package at.lukasberger.bukkit.pvp.events.player.spectator;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPSpectatorMoveEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerMove(PlayerMoveEvent e)
    {
        // check if spectating is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-spectating"))
            return;

        // check if player is null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerSpectating(e.getPlayer()))
            return;

        // check if player really moved
        if(e.getFrom().getBlockX() == e.getTo().getBlockX() && e.getFrom().getBlockY() == e.getTo().getBlockY() && e.getFrom().getBlockZ() == e.getTo().getBlockZ())
            return;

        Arena a = ArenaManager.instance.getArena(InGameManager.instance.getArena(e.getPlayer()));

        // check if spectator is still in arena, if not, cancel move event
        if(locationIsInCuboid(e.getPlayer().getLocation(), a.getMinLocation(), a.getMaxLocation()))
            return;

        e.setCancelled(true);
    }

    // original code: https://bukkit.org/threads/detect-when-players-are-inside-inbetween-two-coordinates.171004/#post-1826170
    public boolean locationIsInCuboid(Location playerLocation, Location min, Location max)
    {
        boolean trueOrNot = false;

        if (playerLocation.getWorld() == min.getWorld() && playerLocation.getWorld() == max.getWorld())
        {
            if (playerLocation.getX() >= min.getX() && playerLocation.getX() <= max.getX())
                if (playerLocation.getY() >= min.getY() && playerLocation.getY() <= max.getY())
                    if (playerLocation.getZ() >= min.getZ() && playerLocation.getZ() <= max.getZ())
                        trueOrNot = true;
        }

        return trueOrNot;
    }

}
