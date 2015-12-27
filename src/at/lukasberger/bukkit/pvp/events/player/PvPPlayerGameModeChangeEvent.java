package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerGameModeChangeEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.player.allow-gm", false))
        {
            if(e.getNewGameMode() != GameMode.SURVIVAL)
            {
                e.setCancelled(true);
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }

}