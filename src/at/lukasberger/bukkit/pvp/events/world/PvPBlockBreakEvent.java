package at.lukasberger.bukkit.pvp.events.world;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPBlockBreakEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockBreakEvent(BlockBreakEvent e)
    {
        // check if player is not null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.block.break", false))
            e.setCancelled(true);
    }

}
