package at.lukasberger.bukkit.pvp.events.inventory;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPInventoryHelpEvents implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryMoveItem(InventoryMoveItemEvent e)
    {
        // only apply actions to players
        if(!(e.getSource().getHolder() instanceof Player))
            return;

        if(!e.getSource().getTitle().equalsIgnoreCase(PvP.inventoryHelpTitle))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryDrag(InventoryDragEvent e)
    {
        // only apply actions to players
        if(!(e.getInventory().getHolder() instanceof Player))
            return;

        if(!e.getInventory().getTitle().equalsIgnoreCase(PvP.inventoryHelpTitle))
            return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onInventoryClick(InventoryClickEvent e)
    {
        // only apply actions to players
        if(!(e.getInventory().getHolder() instanceof Player))
            return;

        if(!e.getInventory().getTitle().equalsIgnoreCase(PvP.inventoryHelpTitle))
            return;

        e.setCancelled(true);
    }

}
