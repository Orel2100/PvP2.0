package at.lukasberger.bukkit.pvp.events.player.spectator;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPSpectatorDamageEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(EntityDamageEvent e)
    {
        // check if spectating is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-spectating"))
            return;

        // check if damaged is player
        if(!(e.getEntity() instanceof Player))
            return;

        // check if damaged is spectating
        if(!InGameManager.instance.isPlayerSpectating((Player) e.getEntity()))
            return;

        // don't damage spectators
        e.setCancelled(true);
    }

}
