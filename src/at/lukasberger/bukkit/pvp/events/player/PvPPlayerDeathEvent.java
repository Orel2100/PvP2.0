package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
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

        InGameManager.instance.getPlayer(killed).addDeath();
        InGameManager.instance.getPlayer(killer).addKill();

        PvP.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PvP.getInstance(), new Runnable() {

            @Override
            public void run() {
                InGameManager.instance.joinArenaOnDeath(killed);
            }

        }, 5L);

        // set some things
        e.setKeepInventory(false);
        e.setKeepLevel(true);
        e.getDrops().clear();
        e.setDroppedExp(0);
        e.setDeathMessage("");
    }

}
