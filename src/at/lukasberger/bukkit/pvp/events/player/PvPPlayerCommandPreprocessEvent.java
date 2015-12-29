package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Created by Lukas on 29.12.2015.
 */
public class PvPPlayerCommandPreprocessEvent implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e)
    {
        // check if player is null
        if (e.getPlayer() == null)
            return;

        // check if player is ingame
        if (!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        // check if commands should be blocked
        if (!PvP.getInstance().getConfig().getBoolean("ingame.commands.block"))
            return;

        // check if player is op and op-overriding is enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.commands.override.op") && e.getPlayer().isOp())
            return;

        // check if player has permission and perm-overriding is enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.commands.override.perm") && e.getPlayer().isOp())
            return;
    }

}
