package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerQuitEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent e)
    {
        if(InGameManager.instance.isPlayerIngame(e.getPlayer()))
            InGameManager.instance.leaveArena(e.getPlayer());

        if(InGameManager.instance.isPlayerSpectating(e.getPlayer()))
            InGameManager.instance.leaveArenaSpectating(e.getPlayer());
    }

}
