package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerFallDamage implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamage(EntityDamageEvent e)
    {
        // check if damaged is not null
        if(e.getEntity() == null)
            return;

        // check if damaged is player
        if(!(e.getEntity() instanceof Player))
            return;

        Player damaged = (Player)e.getEntity();

        // check if damaged is ingame
        if(!InGameManager.instance.isPlayerIngame(damaged))
            return;

        if(e.getCause() == EntityDamageEvent.DamageCause.FALL)
        {
            if(!PvP.getInstance().getConfig().getBoolean("ingame.fall-damage", false))
                e.setCancelled(true);
        }
    }

}
