package at.lukasberger.bukkit.pvp.events.world;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPBlockPlaceEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockPlace(BlockPlaceEvent e)
    {
        // check if player is not null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.block.place", false))
            e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockMultiPlace(BlockMultiPlaceEvent e)
    {
        // check if player is not null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.block.place", false))
            e.setCancelled(true);
    }

}