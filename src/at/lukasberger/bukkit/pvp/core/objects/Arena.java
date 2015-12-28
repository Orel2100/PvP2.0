package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class Arena
{

    private Config arenaConfig;
    private String arenaName;

    public static Arena createArena(Selection weSelection, String name)
    {
        Config newArenaConfig = new Config("arenas/" + name);
        newArenaConfig.saveDefaultConfig("empty");

        // set the first point of the arena
        newArenaConfig.config.set("arena.min.x", weSelection.getMinimumPoint().getX());
        newArenaConfig.config.set("arena.min.y", weSelection.getMinimumPoint().getY());
        newArenaConfig.config.set("arena.min.z", weSelection.getMinimumPoint().getZ());

        // set the second point of the arena
        newArenaConfig.config.set("arena.max.x", weSelection.getMaximumPoint().getX());
        newArenaConfig.config.set("arena.max.y", weSelection.getMaximumPoint().getY());
        newArenaConfig.config.set("arena.max.z", weSelection.getMaximumPoint().getZ());

        // set the name of the world
        newArenaConfig.config.set("arena.world", weSelection.getWorld().getName());

        // set default settings
        newArenaConfig.config.set("game.maxplayers", -1);
        newArenaConfig.config.set("game.allow-block-breaking", false);
        newArenaConfig.config.set("game.on-kill.firework", true);
        newArenaConfig.config.set("game.on-kill.sounds", true);
        newArenaConfig.config.set("game.on-kill.thunder", true);
        newArenaConfig.config.set("game.on-join.firework", false);
        newArenaConfig.config.set("game.on-join.sounds", true);
        newArenaConfig.config.set("game.on-join.thunder", false);

        // save it
        newArenaConfig.saveConfig();
        newArenaConfig.reloadConfig();

        newArenaConfig.config = null;
        newArenaConfig.configFile = null;

        Arena arena = new Arena(name);
        ArenaManager.instance.loadArena(name, arena);

        // return instance of new Arena
        return arena;
    }

    // creates a new instance of an arena
    public Arena(String name)
    {
        arenaConfig = new Config("arenas/" + name);
        this.arenaName = name;
    }

    // completly removes the arena
    public void delete()
    {
        arenaConfig.configFile.delete();
    }

    // checks if the arena exists
    public boolean doesArenaExists()
    {
        return arenaConfig.exists();
    }

    // short version for change a setting
    public void changeConfig(String name, Object value)
    {
        arenaConfig.config.set(name, value);
    }

    public void changeSelection(Selection weSelection)
    {
        // set the first point of the arena
        this.changeConfig("arena.min.x", weSelection.getMinimumPoint().getX());
        this.changeConfig("arena.min.y", weSelection.getMinimumPoint().getY());
        this.changeConfig("arena.min.z", weSelection.getMinimumPoint().getZ());

        // set the second point of the arena
        this.changeConfig("arena.max.x", weSelection.getMaximumPoint().getX());
        this.changeConfig("arena.max.y", weSelection.getMaximumPoint().getY());
        this.changeConfig("arena.max.z", weSelection.getMaximumPoint().getZ());

        // set the name of the world
        this.changeConfig("arena.world", weSelection.getWorld().getName());

        arenaConfig.saveConfig();
    }

    public int addSpawn(Location loc)
    {
        List<String> spawns = arenaConfig.config.getStringList("spawns");
        spawns.add(loc.getX() + ";" + loc.getY() + ";" + loc.getZ());

        arenaConfig.config.set("spawns", spawns);
        arenaConfig.saveConfig();

        return spawns.size();
    }

    public int removeLastSpawn()
    {
        List<String> spawns = arenaConfig.config.getStringList("spawns");

        if(spawns.size() == 0)
            return -1;

        spawns.remove(spawns.size() - 1);

        arenaConfig.config.set("spawns", spawns);
        arenaConfig.saveConfig();

        return spawns.size() + 1;
    }

    public void teleportPlayer(Player p)
    {
        // Load world
        World arenaworld = Bukkit.getServer().getWorld(arenaConfig.config.getString("arena.world"));

        // load points of arena
        double minX = arenaConfig.config.getDouble("arena.min.x");
        double minZ = arenaConfig.config.getDouble("arena.max.z");

        double maxX = arenaConfig.config.getDouble("arena.min.x");
        double maxZ = arenaConfig.config.getDouble("arena.max.z");

        // do not teleport a player to a non-existing world
        if(arenaworld != null)
        {
            // check if any predefined spawn is available
            if(arenaConfig.config.getStringList("spawns") != null && arenaConfig.config.getStringList("spawns").size() != 0)
            {
                // choose a random spawn point
                List<String> locations = arenaConfig.config.getStringList("spawns");
                int randLoc = Math.min(0, locations.size() - 1) + (int)Math.round((1 + Math.abs(0 - locations.size() - 1)) * Math.random());

                while(randLoc >= locations.size())
                    randLoc = Math.min(0, locations.size() - 1) + (int)Math.round((1 + Math.abs(0 - locations.size() - 1)) * Math.random());

                String[] locationParts = locations.get(randLoc).split(";");

                // activaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, Double.parseDouble(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2])));

                // deactivaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);
            }
            else
            {
                // choose random spawn location
                // calculate the random spawn-point
                double x = Math.min(minX, maxX) + (double)Math.round(-0.5f + (1 + Math.abs(minX - maxX)) * Math.random());
                double z = Math.min(minZ, maxZ) + (double)Math.round(-0.5f + (1 + Math.abs(minZ - maxZ)) * Math.random());
                int y = arenaworld.getHighestBlockYAt((int)x, (int)z);

                // activaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, x, y, z));

                // deactivaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);
            }
        }
        else
        {
            // world does not exists
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get("commands.error.no-world", arenaConfig.config.getString("arena.world")));
        }
    }

}
