package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.core.objects.Arena;

import java.util.HashMap;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class ArenaManager
{

    // lists
    private HashMap<String, Arena> arenas = new HashMap<>();

    // instance
    public static ArenaManager instance = new ArenaManager();

    // disallow creation of other instances
    private ArenaManager() { }

    /**
     * Loads the given arena into memory
     * @param arena The arena
     */
    public void loadArena(String arena)
    {
        arenas.put(arena, new Arena(arena));
    }

    /**
     * Puts the given arena into the internal lists
     * @param name The name of the arena
     * @param arena The arena
     */
    public void loadArena(String name, Arena arena)
    {
        if(arena.isRankedArena())
            arena.startQueue();

        arenas.put(name, arena);
    }

    /**
     * Loads the arena into memory if not loaded and returns the instance
     * @param arena The name of the arena
     * @return The instance of the arena-object
     */
    public Arena getArena(String arena)
    {
        if(!isArenaLoaded(arena))
            loadArena(arena);

        return arenas.get(arena);
    }

    /**
     * Removes the arena from the internal lists
     * @param arena The name of the arena
     */
    public void unloadArena(String arena)
    {
        arenas.remove(arena);
    }

    /**
     * Removes all arenas from the internal lists
     */
    public void unloadAllArenas()
    {
        for(String key : arenas.keySet())
            arenas.remove(key);
    }


    /**
     * Deletes the arena and removes it from the lists
     * @param arena The name of the arena
     */
    public void deleteArena(String arena)
    {
        if(!isArenaLoaded(arena))
            loadArena(arena);

        Arena a = arenas.get(arena);
        a.delete();
        arenas.remove(arena);
    }

    /**
     * Indicates if the arena is loaded
     * @param arena The name of the arena
     * @return If the arena is loaded or not
     */
    public boolean isArenaLoaded(String arena)
    {
        return arenas.containsKey(arena);
    }

}
