package at.lukasberger.bukkit.pvp.events.player.party;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.PartyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPartyPlayerQuitEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent e)
    {
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-parties"))
            return;

        if(!PartyManager.instance.isPlayerInAnyParty(e.getPlayer()))
            return;

        Long party = PartyManager.instance.getPartyID(e.getPlayer());

        if(PartyManager.instance.isPartyLeader(e.getPlayer()))
        {
            PartyManager.instance.delete(e.getPlayer());
        }
        else
        {
            PartyManager.instance.leave(e.getPlayer());
        }
    }

}
