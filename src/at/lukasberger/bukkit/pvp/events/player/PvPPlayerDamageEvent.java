package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerDamageEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        // check if damaged AND damager are not null
        if(e.getEntity() == null || e.getDamager() == null)
            return;

        // check if damaged AND damager are players
        if(!(e.getEntity() instanceof Player) || (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)))
            return;

        Player damaged = (Player)e.getEntity();
        Player damager = null;

        if(e.getDamager() instanceof Arrow)
            damager = (Player)((Arrow) e.getDamager()).getShooter();
        else
            damager = (Player)e.getDamager();

        // check if damaged AND damager are ingame
        if(!InGameManager.instance.isPlayerIngame(damaged) || !InGameManager.instance.isPlayerIngame(damager))
        {
            e.setCancelled(true);
            return;
        }

        if(damaged.getHealth() - e.getFinalDamage() <= 0.0) // player would be dead
        {
            if(!PvP.getInstance().getConfig().getBoolean("ingame.show-death-screen")) // rejoin player without death-screen
            {
                Arena a = InGameManager.instance.getArena(damaged);
                if(PvP.getInstance().getConfig().getBoolean("ingame.enable-elo") || a.isRankedArena())
                {
                    Integer damaged_elo = InGameManager.instance.getPlayer(damaged).getElo();
                    Integer damager_elo = InGameManager.instance.getPlayer(damager).getElo();

                    InGameManager.instance.getPlayer(damaged).updateElo(damager_elo, false);
                    InGameManager.instance.getPlayer(damager).updateElo(damaged_elo, true);

                    damaged.sendMessage(PvP.prefix + MessageManager.instance.get(damaged, "ingame.ranking.lost", InGameManager.instance.getPlayer(damaged).getElo(), damager.getName()));
                    damager.sendMessage(PvP.prefix + MessageManager.instance.get(damaged, "ingame.ranking.won", InGameManager.instance.getPlayer(damager).getElo(), damaged.getName()));
                }

                e.setCancelled(true);
                ((Player) e.getEntity()).setHealth(20.0);

                InGameManager.instance.joinArenaOnDeath(damaged); // teleport player to arena
                InGameManager.instance.getPlayer(damaged).addDeath().updateScoreboard(); // add death to killed player
                InGameManager.instance.getPlayer(damager).addKill().updateScoreboard(); // add kill to "winning" player
            }
        }
    }


}
