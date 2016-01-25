package at.lukasberger.bukkit.pvp.events.inventory;

import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPItemPickupEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryPickupItem(InventoryPickupItemEvent e)
    {
        // only apply actions to players
        if(e.getInventory().getHolder() instanceof Player)
            return;

        // check if player is ingame
        if(InGameManager.instance.isPlayerIngame((Player)e.getInventory().getHolder()))
            return;

        e.setCancelled(true);
    }

}
