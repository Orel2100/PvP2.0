package at.lukasberger.bukkit.pvp.events.player.afk;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.AfkManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Lukas on 01.01.2016.
 */
public class PvPPlayerAfkInteractEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent e)
    {
        // check if player is null
        if(e.getPlayer() == null)
            return;

        // check if afk is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.enable-afk"))
            return;

        // check if this indicator is enabled
        if(!PvP.getInstance().getConfig().getBoolean("ingame.afk.indicators.interact"))
            return;

        // check if player is ingame
        if(!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        AfkManager.instance.updateAfk(e.getPlayer());
    }

}
