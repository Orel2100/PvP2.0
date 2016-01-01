package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class AfkManager
{

    private int checkTask = Integer.MIN_VALUE;
    private int notifyTask = Integer.MIN_VALUE;

    private HashMap<String, LocalDateTime> afk = new HashMap<>();

    // instance
    public static AfkManager instance = new AfkManager();

    // disallow creation of other instances
    private AfkManager() { }

    // starts the tasks
    public void startTasks()
    {
        checkTask = PvP.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(PvP.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                for(String player : afk.keySet())
                {
                    LocalDateTime playerTime = afk.get(player).plusSeconds(1);

                    afk.remove(player);
                    afk.put(player, playerTime);
                }
            }

        }, 0L, 20L);

        // TODO: Check if everything is threading-safe
        notifyTask = PvP.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(PvP.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                for (String uuid : afk.keySet())
                {
                    if (!hasPlayerGoneAfkNow(uuid))
                        continue;

                    Player p = PvP.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    p.sendMessage(MessageManager.instance.get(p, "ingame.afk.marked"));
                }
            }

        }, 0L, 20L).getTaskId();
    }

    // stops the tasks
    public void stopTasks()
    {
        if(checkTask != Integer.MIN_VALUE)
            PvP.getInstance().getServer().getScheduler().cancelTask(checkTask);

        if(notifyTask != Integer.MIN_VALUE)
            PvP.getInstance().getServer().getScheduler().cancelTask(notifyTask);
    }

    public boolean hasPlayerGoneAfkNow(String uuid)
    {
        return ChronoUnit.SECONDS.between(afk.get(uuid), LocalDateTime.now()) == PvP.getInstance().getConfig().getInt("ingame.afk.idle-period");
    }

    public boolean isPlayerAfk(Player p)
    {
        return ChronoUnit.SECONDS.between(afk.get(p.getUniqueId().toString()), LocalDateTime.now()) > PvP.getInstance().getConfig().getInt("ingame.afk.idle-period");
    }

    // marks a player as AFK
    public void afk(Player p)
    {
        if(!afk.containsKey(p.getUniqueId().toString()))
            afk.put(p.getUniqueId().toString(), LocalDateTime.now());
    }

    // marks a player as active/un-AFK
    public void unafk(Player p)
    {
        if(afk.containsKey(p.getUniqueId().toString()))
            afk.remove(p.getUniqueId().toString());
    }

    // updates the last afk-timestamp
    public void updateAfk(Player p)
    {
        unafk(p);
        afk(p);
    }

}
