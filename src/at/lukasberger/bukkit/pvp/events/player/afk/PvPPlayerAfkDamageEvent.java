package at.lukasberger.bukkit.pvp.events.player.afk;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.AfkManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by Lukas on 01.01.2016.
 */
public class PvPPlayerAfkDamageEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        // check if entity is null
        if(e.getEntity() == null)
            return;

        // check if damaged is player
        if(!(e.getEntity() instanceof Player))
            return;

        // check if afk is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-afk"))
            return;

        Player p = (Player)e.getEntity();

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(p))
            return;

        // check if player is afk
        if(!AfkManager.instance.isPlayerAfk(p))
            return;

        e.setCancelled(true);

        // check if damager is player and send message if enabled
        if(e.getDamager() instanceof Player && PvP.getInstance().getConfig().getBoolean("ingame.afk.notify-damager"))
        {
            Player damager = (Player)e.getDamager();
            damager.sendMessage(ChatColor.RED + MessageManager.instance.get(damager, "ingame.afk.damager-notify", p.getName()));
        }
    }

}
