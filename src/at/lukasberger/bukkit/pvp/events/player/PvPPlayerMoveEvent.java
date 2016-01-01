package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerMoveEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerMove(PlayerMoveEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.player.allow-hunger", false))
            e.getPlayer().setSaturation(20.0F);
    }

}

