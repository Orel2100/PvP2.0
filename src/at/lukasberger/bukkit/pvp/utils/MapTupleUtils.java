package at.lukasberger.bukkit.pvp.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class MapTupleUtils<A, B>
{

    public boolean containsTuple(List<MapTuple<A, B>> list, A key, B value)
    {
        for(MapTuple<A, B> item : list)
            if(item.key().equals(key) && item.value().equals(value))
                return true;

        return false;
    }

    public List<MapTuple<A, B>> removeTuple(List<MapTuple<A, B>> list, A key, B value)
    {
        List<MapTuple<A, B>> filteredList = new ArrayList<>();

        for(MapTuple<A, B> item : list)
            if(!item.key().equals(key) && !item.value().equals(value))
                filteredList.add(item);

        return filteredList;
    }

    public boolean containsKey(List<MapTuple<A, B>> list, A key)
    {
        for(MapTuple<A, B> item : list)
            if(item.key().equals(key))
                return true;

        return false;
    }

    public boolean containsValue(List<MapTuple<A, B>> list, B value)
    {
        for(MapTuple<A, B> item : list)
            if(item.value().equals(value))
                return true;

        return false;
    }

    public List<MapTuple<A, B>> getTuplesEqualingKey(List<MapTuple<A, B>> list, A key)
    {
        List<MapTuple<A, B>> filteredList = new ArrayList<>();

        for(MapTuple<A, B> item : list)
            if(item.key().equals(key))
                filteredList.add(item);

        return filteredList;
    }

}
