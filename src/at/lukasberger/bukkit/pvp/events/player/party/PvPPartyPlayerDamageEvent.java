package at.lukasberger.bukkit.pvp.events.player.party;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.PartyManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPartyPlayerDamageEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        // check if parties are enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-parties"))
            return;

        // check if damaged AND damager are not null
        if(e.getEntity() == null || e.getDamager() == null)
            return;

        // check if damaged AND damager are players
        if(!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player))
            return;

        Player damaged = (Player)e.getEntity();
        Player damager = (Player)e.getDamager();

        // check if damaged AND damager are ingame AND if they are in the same arena
        if(!InGameManager.instance.isPlayerIngame(damaged) || !InGameManager.instance.isPlayerIngame(damager) ||
                !InGameManager.instance.getArena(damaged).getName().equalsIgnoreCase(InGameManager.instance.getArena(damager).getName()))
        {
            e.setCancelled(true);
            return;
        }

        Arena a = InGameManager.instance.getArena(damaged);

        // check if damage between party-members is enabled in arena
        if(a.getGameConfiguration().getBoolean("party-damage"))
            return;

        Long damageParty = PartyManager.instance.getPartyID(damaged);
        Long damagerParty = PartyManager.instance.getPartyID(damager);

        // check if they are in the same party
        if(damageParty != damagerParty)
            return;

        e.setDamage(0.0);
        e.setCancelled(true);
    }

}
