package at.lukasberger.bukkit.pvp.events.player;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

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

        // check if commands should be blocked
        if (!PvP.getInstance().getConfig().getBoolean("ingame.commands.block"))
            return;

        // check if player is ingame
        if (!InGameManager.instance.isPlayerIngame(e.getPlayer()))
            return;

        // check if player is op and op-overriding is enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.commands.override.op") && e.getPlayer().isOp())
            return;

        // check if player has permission and perm-overriding is enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.commands.override.perm") && e.getPlayer().hasPermission("pvp.player.commands.override"))
            return;

        String commandline = e.getMessage().substring(1); // remove slash from begin
        String command = commandline.split(" ")[0].toLowerCase();
        List<String> whitelistedCommands = PvP.getInstance().getConfig().getStringList("ingame.commands.whitelist");

        boolean cancel = true;

        for(String whitelistCommand : whitelistedCommands)
        {
            if(whitelistCommand.endsWith("*")) // only begin should be checked
            {
                if(command.startsWith(whitelistCommand.substring(0, whitelistCommand.length() - 1).toLowerCase())) // remove placeholder and check
                {
                    cancel = false;
                    break;
                }
            }
            else if(whitelistCommand.startsWith("*")) // only ending should be checked
            {
                if(command.endsWith(whitelistCommand.substring(1).toLowerCase())) // remove placeholder and check
                {
                    cancel = false;
                    break;
                }
            }
            else if(whitelistCommand.endsWith("*") && whitelistCommand.startsWith("*")) // command should contain whitelisted
            {
                if(command.contains(whitelistCommand.substring(1, whitelistCommand.length() - 1).toLowerCase())) // remove placeholders and check
                {
                    cancel = false;
                    break;
                }
            }
            else // whitelist-command without any placeholder
            {
                if(command.equalsIgnoreCase(whitelistCommand.toLowerCase()))
                {
                    cancel = false;
                    break;
                }
            }
        }

        e.setCancelled(cancel);
    }

}
