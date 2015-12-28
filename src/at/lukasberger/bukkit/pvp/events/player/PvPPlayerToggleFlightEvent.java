package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerToggleFlightEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerToggleFlight(PlayerToggleFlightEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.player.allow-fly", false))
        {
            e.setCancelled(true);
            e.getPlayer().setFlying(false);
        }
    }

}