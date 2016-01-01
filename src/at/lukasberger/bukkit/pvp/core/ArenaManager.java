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

    // loads the arena into the list
    public void loadArena(String arena)
    {
        arenas.put(arena, new Arena(arena));
    }

    // loads the arena into the list
    public void loadArena(String name, Arena arena)
    {
        arenas.put(name, arena);
    }

    // returns the arena-object and loads if not available
    public Arena getArena(String arena)
    {
        if(!isArenaLoaded(arena))
            loadArena(arena);

        return arenas.get(arena);
    }

    // unloads the arena from the list
    public void unloadArena(String arena)
    {
        arenas.remove(arena);
    }

    // removes all arena from list
    public void unloadAllArenas()
    {
        for(String key : arenas.keySet())
            arenas.remove(key);
    }

    // completely deletes the arena
    public void deleteArena(String arena)
    {
        Arena a = arenas.get(arena);
        a.delete();
        arenas.remove(arena);
    }

    public boolean isArenaLoaded(String arena)
    {
        return arenas.containsKey(arena);
    }

}
