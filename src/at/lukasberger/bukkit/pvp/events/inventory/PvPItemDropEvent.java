package at.lukasberger.bukkit.pvp.events.inventory;

import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPItemDropEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDropItem(PlayerDropItemEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame((Player)e.getPlayer()))
            return;

        e.setCancelled(true);
    }

}
