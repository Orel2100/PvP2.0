package at.lukasberger.bukkit.pvp.utils;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class MapTuple<A, B>
{

    private A _key;
    private B _value;

    public A key()
    {
        return _key;
    }

    public B value()
    {
        return _value;
    }

    public MapTuple(A a, B b)
    {
        this._key = a;
        this._value = b;
    }

}
