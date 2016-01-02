package at.lukasberger.bukkit.pvp.events.player.items;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerBowEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onProjectileLaunch(ProjectileLaunchEvent e)
    {
        // check if shooter is null
        if(e.getEntity().getShooter() == null)
            return;

        // check if shooter is player
        if(!(e.getEntity().getShooter() instanceof Player))
            return;

        PvP.getInstance().getLogger().info("Type: " + e.getEntityType());

        // check if it is arrow
        if(e.getEntity().getType() != EntityType.ARROW)
            return;

        Player p = (Player)e.getEntity().getShooter();

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(p))
            return;

        // check if bow-gravity is disabled
        if(!PvP.getInstance().getConfig().getBoolean("gadgets.bow.disable-gravity"))
            return;

        e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(5));
    }

}
