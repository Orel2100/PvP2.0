package at.lukasberger.bukkit.pvp.events.player.spectator;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPSpectatorGameModeChangeEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerGameModeChange(PlayerGameModeChangeEvent e)
    {
        // check if spectating is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-spectating"))
            return;

        // check if player is null
        if(e.getPlayer() == null)
            return;

        if(!InGameManager.instance.isPlayerSpectating(e.getPlayer()))
            return;

        GameMode specGM = GameMode.valueOf(PvP.getInstance().getConfig().getString("ingame.spectating.gamemode").toUpperCase());

        if(e.getNewGameMode() != specGM)
        {
            e.setCancelled(true);
            e.getPlayer().setGameMode(specGM);
        }
    }

}
