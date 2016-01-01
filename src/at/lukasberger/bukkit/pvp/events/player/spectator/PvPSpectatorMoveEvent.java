package at.lukasberger.bukkit.pvp.events.player.spectator;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import at.lukasberger.bukkit.pvp.utils.MapTuple;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

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
        Arena a = ArenaManager.instance.getArena(InGameManager.instance.getArena(e.getPlayer()));

        Location loc = e.getTo();
        Location v1 = a.getMinLocation();
        Location v2 = a.getMaxLocation();

        PvP.getInstance().getLogger().info("Moving: " + loc.getBlockX() + "; " + loc.getBlockY() + "; " + loc.getBlockZ());

        /* if(loc.getBlockX() > v1.getBlockX())
            e.getPlayer().teleport(new Location(loc.getWorld(), v1.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()));
        else if(loc.getBlockX() < v2.getBlockX())
            e.getPlayer().teleport(new Location(loc.getWorld(), v2.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ())); */

        // X-Limiting
        if(loc.getBlockX() < v1.getBlockX())
        {
            e.setFrom(new Location(loc.getWorld(), v1.getBlockX() - 1, loc.getBlockY(), loc.getBlockZ()));
            e.setCancelled(true);
        }
        else if (loc.getBlockX() > v2.getBlockX())
        {
            e.setFrom(new Location(loc.getWorld(), v2.getBlockX() + 1, loc.getBlockY(), loc.getBlockZ()));
            e.setCancelled(true);
        }

        // Y-Limiting
        if(loc.getBlockY() < v1.getBlockY())
        {
            e.setFrom(new Location(loc.getWorld(), loc.getBlockX(), v1.getBlockY() - 1, loc.getBlockZ()));
            e.setCancelled(true);
        }
        else if (loc.getBlockY() > v2.getBlockY())
        {
            e.setFrom(new Location(loc.getWorld(), loc.getBlockX(), v2.getBlockY() + 1, loc.getBlockZ()));
            e.setCancelled(true);
        }

        // Z-Limiting
        if(loc.getBlockZ() < v1.getBlockZ())
        {
            e.setFrom(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), v1.getBlockZ() - 1));
            e.setCancelled(true);
        }
        else if (loc.getBlockZ() > v2.getBlockZ())
        {
            e.setFrom(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), v2.getBlockZ() + 1));
            e.setCancelled(true);
        }

        /* else if(loc.getBlockZ() > v1.getBlockZ())
            e.getPlayer().teleport(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), v1.getBlockZ() - 1));
        else if(loc.getBlockZ() < v2.getBlockZ())
            e.getPlayer().teleport(new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), v2.getBlockZ() + 1)); */
    }

}
