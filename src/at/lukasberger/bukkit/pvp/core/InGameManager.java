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

import java.util.*;

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
    private HashMap<String, String> playerGamemodeBeforeJoin = new HashMap<>();
    private List<String> playerTeleportStatus = new ArrayList<>();

    // instance
    public static InGameManager instance = new InGameManager();

    // disallow creation of other instances
    private InGameManager() { }

    /**
     * Loads and returns the instance of a PvP-Player
     * @param p The "Bukkit"-Player
     * @return Instance of the Player as PvP-Player
     */
    public PvPPlayer getPlayer(Player p)
    {
        if(!PlayerManager.instance.isPlayerLoaded(p.getUniqueId().toString()))
            PlayerManager.instance.loadPlayer(
                    p
            );

        // return the player
        return PlayerManager.instance.getPlayer(p);
    }

    /**
     * Indicates if the player is ingame
     * @param p The Player
     * @return If the player is ingame or not
     */
    public boolean isPlayerIngame(Player p)
    {
        return currentPlayerArena.containsKey(p.getUniqueId().toString());
    }

    /**
     * Indicates if the player is spectating
     * @param p The Player
     * @return If the player is spectating or not
     */
    public boolean isPlayerSpectating(Player p)
    {
        return currentSpectatorArena.containsKey(p.getUniqueId().toString());
    }

    /**
     * Sends the player to the arena
     * @param p The Player
     * @param arenaName The name of the arena
     * @return If joining was successful or not
     */
    public boolean joinArena(Player p, String arenaName)
    {
        // if player already is in arena, do not join
        if(currentPlayerArena.containsKey(p.getUniqueId().toString()))
        {
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.already-ingame", arenaName));
            return false;
        }

        // Load arena configuration and teleport player
        Arena arena = ArenaManager.instance.getArena(arenaName);

        // check if arena exists
        if(!arena.doesArenaExists())
        {
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "ingame.no-arena", arenaName));
            return false;
        }

        Integer joinResult = arena.addPlayer(p, PartyManager.instance.getPartyID(p));

        // run some arena-specific checks
        if(joinResult == -1)
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "ingame.error.arena-full", arenaName));
        else if(joinResult == -2)
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.error.parties-only", arenaName));
        else if(joinResult == -3)
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.error.party-leader-only", arenaName));
        else if(joinResult == -4)
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.error.party-too-large", arena.getGameConfiguration().getInt("party.size"), arenaName));
        else if(joinResult == -5)
            p.sendMessage(PvP.warningPrefix + MessageManager.instance.get(p, "ingame.error.party-too-small", arena.getGameConfiguration().getInt("party.size"), arenaName));
        else if(joinResult == -10)
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "ingame.ranking.match-running", arenaName));
        else if(joinResult == 1)
            p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.ranking.queue-joined", arenaName));

        if(joinResult == 1)
            return true; // joined queue
        else if(joinResult < 0)
            return false;

        // check if parties enabled
        if(PvP.getInstance().getConfig().getBoolean("ingame.enable-parties"))
        {
            if(PartyManager.instance.isPartyLeader(p)) // teleport party-members to arena if leader joined
                PartyManager.instance.memberMassJoin(p, arenaName);
            else if(PartyManager.instance.getPartyID(p) != -1)
                p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "ingame.error.party-leader-only", arenaName));
        }

        // Put player to lists
        currentPlayerArena.put(p.getUniqueId().toString(), arenaName);
        playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-inv", p.getInventory().getContents());
        playerInventoryBeforeJoin.put(p.getUniqueId().toString() + "-armor", p.getInventory().getArmorContents());
        playerLocationBeforeJoin.put(p.getUniqueId().toString(), p.getLocation());
        playerGamemodeBeforeJoin.put(p.getUniqueId().toString(), p.getGameMode().toString());

        p.getInventory().clear();
        p.setGameMode(GameMode.SURVIVAL);

        arena.teleportPlayer(p, false);
        getPlayer(p).giveCurrentKit();

        p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.joined", arenaName));

        // update the scoreboard
        getPlayer(p).updateScoreboard();

        // set player afk at joining
        AfkManager.instance.afk(p);

        return true;
    }

    /**
     * Sends the player to the arena as a spectator
     * @param p The Player
     * @param arenaName The name of the arena
     * @return If joining was successful or not
     */
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
            playerGamemodeBeforeJoin.put(p.getUniqueId().toString(), p.getGameMode().toString());

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
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "ingame.no-arena", arenaName));
            return false;
        }
    }

    /**
     * Resends the death player to his last arena
     * @param p The Player
     */
    public void joinArenaOnDeath(Player p)
    {
        // get the arena of the player
        String arenaName = currentPlayerArena.get(p.getUniqueId().toString());

        // clear inventory
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        // teleport the player
        Arena arena = ArenaManager.instance.getArena(arenaName);
        arena.teleportPlayer(p, true);
        getPlayer(p).giveCurrentKit();

        // update the scoreboard
        getPlayer(p).updateScoreboard();
    }

    /**
     * Changes the teleport-permission for the player
     * @param p The player
     * @param status The new teleport-status
     */
    public void changeTeleportStatus(Player p, boolean status)
    {
        if(status)
            playerTeleportStatus.add(p.getUniqueId().toString());
        else
            playerTeleportStatus.remove(p.getUniqueId().toString());
    }

    /**
     * Indicates if the player is allowed to be teleported by plugin
     * @param p The player
     * @return If he is allowed to be teleported or not
     */
    public boolean canTeleport(Player p)
    {
        return playerTeleportStatus.contains(p.getUniqueId().toString());
    }

    /**
     * Returns the current arena of the given player (ingame/spectating)
     * @param p The player
     * @return The current arena of the player, null if not ingame/spectating
     */
    public Arena getArena(Player p)
    {
        if(isPlayerIngame(p))
            return ArenaManager.instance.getArena(currentPlayerArena.get(p.getUniqueId().toString()));
        else if(isPlayerSpectating(p))
            return ArenaManager.instance.getArena(currentSpectatorArena.get(p.getUniqueId().toString()));
        else
            return null;
    }

    /**
     * Removes player from internal lists, restores inventory, last position etc.
     * @param p The player
     */
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
        String gamemode = playerGamemodeBeforeJoin.get(p.getUniqueId().toString());

        // remove from lists
        getArena(p).removePlayer(p);
        currentPlayerArena.remove(p.getUniqueId().toString());
        playerInventoryBeforeJoin.remove(p.getUniqueId().toString());
        playerLocationBeforeJoin.remove(p.getUniqueId().toString());
        playerGamemodeBeforeJoin.remove(p.getUniqueId().toString());
        AfkManager.instance.unafk(p);

        // Restore player settings
        p.setGameMode(GameMode.valueOf(gamemode));

        // Restore the inventory which is saved before joining
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inv);

        // remove scoreboard
        p.setScoreboard(PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard());

        // Teleport player to last location before joining
        p.teleport(loc);
        p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.left"));
    }

    /**
     * Removes player from internal lists, restores inventory, last position etc.
     * @param p The player
     */
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
        String gamemode = playerGamemodeBeforeJoin.get(p.getUniqueId().toString());

        // remove from lists
        currentSpectatorArena.remove(p.getUniqueId().toString());
        playerInventoryBeforeJoin.remove(p.getUniqueId().toString());
        playerLocationBeforeJoin.remove(p.getUniqueId().toString());
        playerGamemodeBeforeJoin.remove(p.getUniqueId().toString());

        // Restore player settings
        p.setGameMode(GameMode.valueOf(gamemode));

        // Restore the inventory which is saved before joining
        p.getInventory().clear();
        p.getInventory().setArmorContents(new ItemStack[] { });

        p.getInventory().setArmorContents(armor);
        p.getInventory().setContents(inv);

        // remove scoreboard
        p.setScoreboard(PvP.getInstance().getServer().getScoreboardManager().getNewScoreboard());

        // Teleport player to last location before joining
        p.teleport(loc);
        p.sendMessage(PvP.successPrefix + MessageManager.instance.get(p, "ingame.left"));
    }

    /**
     * Removes all players from the game
     */
    public void leaveArenaAll()
    {
        Set<String> tmpPlayers = new TreeSet<>();
        Set<String> tmpSpectators = new TreeSet<>();

        tmpPlayers.addAll(currentPlayerArena.keySet());
        tmpSpectators.addAll(currentSpectatorArena.keySet());

        for(String pl : tmpPlayers)
            leaveArena(Bukkit.getPlayer(UUID.fromString(pl)));

        for(String pl : tmpSpectators)
            leaveArenaSpectating(Bukkit.getPlayer(UUID.fromString(pl)));
    }

}
