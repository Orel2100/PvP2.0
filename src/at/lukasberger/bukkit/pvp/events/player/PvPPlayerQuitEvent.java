package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Lukas on 28.12.2015.
 */
public class PvPPlayerQuitEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent e)
    {
        if(InGameManager.instance.isPlayerIngame(e.getPlayer()))
            InGameManager.instance.leaveArena(e.getPlayer());
    }

}
