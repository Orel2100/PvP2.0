package at.lukasberger.bukkit.pvp.events.player.items;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PvPPlayerGrenadeEvents implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent e)
    {
        // check if player is null
        if (e.getPlayer() == null)
            return;

        // check if player is ingame
        if (!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        // check if grenades are enabled
        if (!PvP.getInstance().getConfig().getBoolean("gadgets.grenades.enable"))
            return;

        // checks if a item is available
        if(!e.hasItem())
            return;

        // checks if a item is grenade-item
        if(e.getItem().getType() != Material.EGG)
            return;

        ItemStack item = e.getItem();
        item.setAmount(item.getAmount() + 1);
        e.getPlayer().getInventory().setItemInHand(item);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerEggThrown(PlayerEggThrowEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        // check if grenades are enabled
        if(!PvP.getInstance().getConfig().getBoolean("gadgets.grenades.enable"))
            return;

        Vector direction = e.getPlayer().getLocation().getDirection().multiply(PvP.getInstance().getConfig().getDouble("gadgets.grenades.settings.throw-multiplier"));

        e.setHatching(false);
        e.getEgg().setVelocity(direction);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent e)
    {
        // check if player is null
        if(e.getEntity().getShooter() == null)
            return;

        if(!(e.getEntity().getShooter() instanceof Player))
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame((Player)e.getEntity().getShooter()))
            return;

        // check if grenades are enabled
        if(!PvP.getInstance().getConfig().getBoolean("gadgets.grenades.enable"))
            return;

        // check if the entity is an egg
        if(e.getEntity().getType() != EntityType.EGG)
            return;

        double x = e.getEntity().getLocation().getX();
        double y = e.getEntity().getLocation().getY();
        double z = e.getEntity().getLocation().getZ();
        double power = PvP.getInstance().getConfig().getDouble("gadgets.grenades.settings.power");
        boolean setFire = PvP.getInstance().getConfig().getBoolean("gadgets.grenades.settings.fire");
        boolean breakBlocks = PvP.getInstance().getConfig().getBoolean("gadgets.grenades.settings.block-destroy");

        e.getEntity().getWorld().createExplosion(x, y, z, (float)power, setFire, breakBlocks);
    }

}
