package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Clock;
import java.time.LocalDate;
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

    /**
     * Stops all required tasks for the AFK-System
     */
    public void startTasks()
    {
        notifyTask = PvP.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(PvP.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                for (String uuid : afk.keySet())
                {
                    if (!hasPlayerGoneAfkNow(uuid))
                        continue;

                    Player p = PvP.getInstance().getServer().getPlayer(UUID.fromString(uuid));
                    p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.afk.marked"));

                    if(PvP.getInstance().getConfig().getBoolean("ingame.scoreboard.update.on-afk"))
                        InGameManager.instance.getPlayer(p).updateScoreboard();
                }
            }

        }, 0L, 20L);
    }

    /**
     * Stops all tasks of the AFK-System
     */
    public void stopTasks()
    {
        if(checkTask != Integer.MIN_VALUE)
            PvP.getInstance().getServer().getScheduler().cancelTask(checkTask);

        if(notifyTask != Integer.MIN_VALUE)
            PvP.getInstance().getServer().getScheduler().cancelTask(notifyTask);
    }

    /**
     * Indicates if the player has gone AFK right now
     * @param uuid The UUID of the player
     * @return If the player has gone AFK right now
     */
    public boolean hasPlayerGoneAfkNow(String uuid)
    {
        if(!afk.containsKey(uuid))
            return false;

        return ChronoUnit.SECONDS.between(afk.get(uuid), LocalDateTime.now()) == PvP.getInstance().getConfig().getInt("ingame.afk.idle-period");
    }

    /**
     * Indicates if the player is AFK
     * @param p The player
     * @return If the player is AFK or not
     */
    public boolean isPlayerAfk(Player p)
    {
        if(!afk.containsKey(p.getUniqueId().toString()))
            return false;

        return ChronoUnit.SECONDS.between(afk.get(p.getUniqueId().toString()), LocalDateTime.now()) >= PvP.getInstance().getConfig().getInt("ingame.afk.idle-period");
    }


    /**
     * Marks the player as afk
     * @param p The player
     */
    public void afk(Player p)
    {
        if(!afk.containsKey(p.getUniqueId().toString()))
            afk.put(p.getUniqueId().toString(), LocalDateTime.now());
    }

    /**
     * Marks the player as not afk
     * @param p The player
     */
    public void unafk(Player p)
    {
        if(isPlayerAfk(p))
        {
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.afk.unmarked"));
            if(PvP.getInstance().getConfig().getBoolean("ingame.scoreboard.update.on-afk"))
                InGameManager.instance.getPlayer(p).updateScoreboard();
        }

        if(afk.containsKey(p.getUniqueId().toString()))
            afk.remove(p.getUniqueId().toString());
    }

    /**
     * Updates the AFK-Timestamp of the player in the lists
     * @param p The player
     */
    public void updateAfk(Player p)
    {
        unafk(p);
        afk(p);
    }

}
