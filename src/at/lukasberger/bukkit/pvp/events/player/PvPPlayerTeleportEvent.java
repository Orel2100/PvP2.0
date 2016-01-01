package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerTeleportEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerTeleport(PlayerTeleportEvent e)
    {
        // check if player is not null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()) && !InGameManager.instance.isPlayerSpectating(e.getPlayer()))
            return;

        // check if the player is allowed to teleport (eg. arena-teleport, ...)
        if(e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN && InGameManager.instance.canTeleport(e.getPlayer()))
            return;

        if(!PvP.getInstance().getConfig().getBoolean("ingame.teleport.allow", false))
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage(PvP.warningPrefix + MessageManager.instance.get(e.getPlayer(), "ingame.teleport.denied"));
        }
        else
        {
            if(PvP.getInstance().getConfig().getBoolean("ingame.teleport.leave-on-tp", true))
                InGameManager.instance.leaveArena(e.getPlayer());
        }
    }

}
