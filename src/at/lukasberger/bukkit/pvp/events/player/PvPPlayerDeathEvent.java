package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerDeathEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerDeath(PlayerDeathEvent e)
    {
        if(!PvP.getInstance().getConfig().getBoolean("ingame.show-death-screen"))
            return; // should never be the case

        // check if killed AND killer are not null
        if(e.getEntity() == null || e.getEntity().getKiller() == null)
            return;

        Player killed = e.getEntity();
        Player killer = e.getEntity().getKiller();

        // check if killed AND killer are ingame
        if(!InGameManager.instance.isPlayerIngame(killed) || !InGameManager.instance.isPlayerIngame(killer))
            return;

        Arena a = InGameManager.instance.getArena(killed);
        if(PvP.getInstance().getConfig().getBoolean("ingame.enable-elo") && a.isRankedArena())
        {
            Integer damaged_elo = InGameManager.instance.getPlayer(killed).getElo();
            Integer damager_elo = InGameManager.instance.getPlayer(killer).getElo();

            InGameManager.instance.getPlayer(killed).updateElo(damager_elo, false);
            InGameManager.instance.getPlayer(killer).updateElo(damaged_elo, true);
        }

        InGameManager.instance.getPlayer(killed).addDeath();
        InGameManager.instance.getPlayer(killer).addKill();

        // set some things
        e.setKeepInventory(false);
        e.setKeepLevel(true);
        e.getDrops().clear();
        e.setDroppedExp(0);
        e.setDeathMessage("");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerRespawn(PlayerRespawnEvent e)
    {
        // check if killed is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        PvP.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PvP.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                InGameManager.instance.joinArenaOnDeath(e.getPlayer());
            }

        }, 10L);
    }

}
