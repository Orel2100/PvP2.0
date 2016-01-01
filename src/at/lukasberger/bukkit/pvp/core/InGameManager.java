package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.objects.Arena;
import at.lukasberger.bukkit.pvp.core.objects.PvPPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class InGameManager
{

    // lists
    private HashMap<String, String> currentPlayerArena = new HashMap<>();
    private HashMap<String, String> currentSpectatorArena = new HashMap<>();

    private HashMap<String, ItemStack[]> playerInventoryBeforeJoin = new HashMap<>();
    private HashMap<String, Location> playerLocationBeforeJoin = new HashMap<>();
    private HashMap<String, GameMode> playerGamemodeBeforeJoin = new HashMap<>();
    private List<String> playerTeleportStatus = new ArrayList<>();

    // instance
    public static InGameManager instance = new InGameManager();

    // disallow creation of other instances
    private InGameManager() { }

    // returns a instance of a
    public PvPPlayer getPlayer(Player p)
    {
        if(!PlayerManager.instance.isPlayerLoaded(p.getUniqueId().toString()))
            PlayerManager.instance.loadPlayer(
                    p,
                    currentPlayerArena.get(p.getName()),
                    playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-inv"),
                    playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-armor"),
                    playerLocationBeforeJoin.get(p.getUniqueId().toString())
            );

        // return the player
        return PlayerManager.instance.getPlayer(p);
    }

    // checks if a player is ingame
    public boolean isPlayerIngame(Player p)
    {
        return currentPlayerArena.containsKey(p.getUniqueId().toString());
    }

    // checks if a player is spectating
    public boolean isPlayerSpectating(Player p)
    {
        return currentSpectatorArena.containsKey(p.getUniqueId().toString());
    }

    // join the arena
    public boolean joinArena(Player p, String arenaName)
    {
        // if player already is in arena, do not join
        if(currentPlayerArena.containsKey(p.getUniqueId().toString()))
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.already-ingame", arenaName));
            return false;
        }

        // check if parties enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.enable-parties"))
        {
            if(PartyManager.instance.isPartyLeader(p)) // teleport party-members to arena if leader joined
                PartyManager.instance.memberMassJoin(p, arenaName);
        }

        // Load arena configuration and teleport player
        Arena arena = ArenaManager.instance.getArena(arenaName);

        if(arena.doesArenaExists())
        {
            // Put player to lists
            currentPlayerArena.put(p.getUniqueId().toString(), arenaName);
            playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-inv", p.getInventory().getContents());
            playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-armor", p.getInventory().getArmorContents());
            playerLocationBeforeJoin.put(p.getUniqueId().toString(), p.getLocation());
            playerGamemodeBeforeJoin.put(p.getUniqueId().toString(), p.getGameMode());

            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);

            arena.teleportPlayer(p);
            getPlayer(p).giveCurrentKit();

            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.joined", arenaName));

            // update the scoreboard
            getPlayer(p).updateScoreboard();

            return true;
        }
        else
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.no-arena", arenaName));
            return false;
        }
    }

    // join the arena in spectate-mode
    public boolean joinArenaSpectating(Player p, String arenaName)
    {
        // if player already is in arena, do not join
        if(currentSpectatorArena.containsKey(p.getUniqueId().toString()))
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.already-spectating", arenaName));
            return false;
        }

        // Load arena configuration and teleport player
        Arena arena = ArenaManager.instance.getArena(arenaName);

        if(arena.doesArenaExists())
        {
            // Put player to lists
            currentSpectatorArena.put(p.getUniqueId().toString(), arenaName);
            playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-inv", p.getInventory().getContents());
            playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-armor", p.getInventory().getArmorContents());
            playerLocationBeforeJoin.put(p.getUniqueId().toString(), p.getLocation());
            playerGamemodeBeforeJoin.put(p.getUniqueId().toString(), p.getGameMode());

            p.getInventory().clear();
            p.setGameMode(GameMode.valueOf(PvP.getInstance().getConfig().getString("ingame.spectating.gamemode").toUpperCase()));
            p.setFlying(PvP.getInstance().getConfig().getBoolean("ingame.spectating.allow-fly"));
            p.setAllowFlight(PvP.getInstance().getConfig().getBoolean("ingame.spectating.allow-fly"));

            arena.spectate(p);
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.spectating", arenaName));

            // update the scoreboard
            getPlayer(p).updateScoreboard();

            return true;
        }
        else
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.no-arena", arenaName));
            return false;
        }
    }

    // rejoins the arena if the player died
    public void joinArenaOnDeath(Player p)
    {
        // get the arena of the player
        String arenaName = currentPlayerArena.get(p.getUniqueId().toString());

        // clear inventory
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        // teleport the player
        Arena arena = ArenaManager.instance.getArena(arenaName);
        arena.teleportPlayer(p);
        getPlayer(p).giveCurrentKit();

        // update the scoreboard
        getPlayer(p).updateScoreboard();
    }

    // set the status of teleport-allowing to the given status
    public void changeTeleportStatus(Player p, boolean status)
    {
        if(status)
            playerTeleportStatus.add(p.getUniqueId().toString());
        else
            playerTeleportStatus.remove(p.getUniqueId().toString());
    }

    public boolean canTeleport(Player p)
    {
        return playerTeleportStatus.contains(p.getUniqueId().toString());
    }

    public String getArena(Player p)
    {
        if(isPlayerIngame(p))
            return currentPlayerArena.get(p.getUniqueId().toString());
        else if(isPlayerSpectating(p))
            return currentSpectatorArena.get(p.getUniqueId().toString());
        else
            return null;
    }

    // removes the player from the arena and restores inventory/location
    public void leaveArena(Player p)
    {
        // check if player is null
        if(p == null)
            return;

        // check if player is in an arena, return if not
        if(!currentPlayerArena.containsKey(p.getUniqueId().toString()))
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.not-ingame"));
            return;
        }

        // check if parties enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.enable-parties"))
        {
            if(PartyManager.instance.isPartyLeader(p)) // remove party-members from arena if leader left
                PartyManager.instance.memberMassLeave(p);
        }

        // Retrieve inventory and location from lists
        ItemStack[] inv = playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-inv");
        ItemStack[] armor = playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-armor");
        Location loc = playerLocationBeforeJoin.get(p.getUniqueId().toString());

        // Restore player settings
        p.setGameMode(playerGamemodeBeforeJoin.get(p.getUniqueId().toString()));

        // Restore the inventory which is saved before joining
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inv);

        // remove from lists
        currentPlayerArena.remove(p.getUniqueId().toString());
        playerInventoryBeforeJoin.remove(p.getUniqueId().toString());
        playerLocationBeforeJoin.remove(p.getUniqueId().toString());

        // remove scoreboard
        p.setScoreboard(PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard());

        // Teleport player to last location before joining
        p.teleport(loc);
        p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.left"));
    }

    // removes the player from the arena and restores inventory/location
    public void leaveArenaSpectating(Player p)
    {
        // check if player is null
        if(p == null)
            return;

        // check if player is in an arena, return if not
        if(!currentSpectatorArena.containsKey(p.getUniqueId().toString()))
        {
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.not-spectating"));
            return;
        }

        // Retrieve inventory and location from lists
        ItemStack[] inv = playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-inv");
        ItemStack[] armor = playerInventoryBeforeJoin.get(p.getUniqueId().toString() + "-armor");
        Location loc = playerLocationBeforeJoin.get(p.getUniqueId().toString());

        // Restore player settings
        p.setGameMode(playerGamemodeBeforeJoin.get(p.getUniqueId().toString()));

        // Restore the inventory which is saved before joining
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inv);

        // remove from lists
        currentSpectatorArena.remove(p.getUniqueId().toString());
        playerInventoryBeforeJoin.remove(p.getUniqueId().toString());
        playerLocationBeforeJoin.remove(p.getUniqueId().toString());

        // remove scoreboard
        p.setScoreboard(PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard());

        // Teleport player to last location before joining
        p.teleport(loc);
        p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.left"));
    }

    // throws out all player from all arenas (reload etc.)
    public void leaveArenaAll()
    {
        for(String pl : currentPlayerArena.keySet())
            leaveArena(Bukkit.getPlayer(UUID.fromString(pl)));

        for(String pl : currentSpectatorArena.keySet())
            leaveArenaSpectating(Bukkit.getPlayer(UUID.fromString(pl)));
    }

}
