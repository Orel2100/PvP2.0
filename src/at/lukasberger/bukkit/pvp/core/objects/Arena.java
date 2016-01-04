package at.lukasberger.bukkit.pvp.core.objects;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.ArenaManager;
import at.lukasberger.bukkit.pvp.core.InGameManager;
import at.lukasberger.bukkit.pvp.core.MessageManager;
import at.lukasberger.bukkit.pvp.core.PartyManager;
import at.lukasberger.bukkit.pvp.utils.FireworkUtils;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * PvP 2.0, Copyright (c) 2015-2016 Lukas Berger, licensed under GPLv3
 */
public class Arena
{

    private Random rand = new Random();
    private Config arenaConfig;
    private String arenaName;
    private List<String> playerList = new ArrayList<>();

    private Boolean rankedMatchRunning = false;
    private Integer queueTaskId = -1;
    private Integer queueCounter = 0;
    private List<String> queuePlayerList = new ArrayList<>();
    private List<String> joiningPlayerList = new ArrayList<>();

    /**
     * Creates an new arena with default settings
     * @param weSelection The WorldEdit-Selection of the arena
     * @param name The name of the arena
     * @return The instance of the new arena-object
     */
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
        newArenaConfig.config.set("game.max-players", -1);
        newArenaConfig.config.set("game.is-ranked", false);

        newArenaConfig.config.set("game.party.only", false);
        newArenaConfig.config.set("game.party.size", -1);
        newArenaConfig.config.set("game.party.damage", true);

        newArenaConfig.config.set("game.firework.kill", true);
        newArenaConfig.config.set("game.firework.join", true);
        newArenaConfig.config.set("game.firework.count", 4);

        newArenaConfig.config.set("game.lightning.kill", true);
        newArenaConfig.config.set("game.lightning.join", false);
        newArenaConfig.config.set("game.lightning.count", 2);

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

    /**
     * Creates an instance of an already exisiting arena
     * @param name Name of the arena
     */
    public Arena(String name)
    {
        arenaConfig = new Config("arenas/" + name);
        this.arenaName = name;

        if(!arenaConfig.exists())
            return;

        if(isRankedArena() && !PvP.isDisabling)
            startQueue();

        if(!arenaConfig.config.contains("game.max-players"))
            arenaConfig.config.set("game.max-players", -1);
        if(!arenaConfig.config.contains("game.is-ranked"))
            arenaConfig.config.set("game.is-ranked", false);

        if(!arenaConfig.config.contains("game.party.only"))
            arenaConfig.config.set("game.party.only", false);
        if(!arenaConfig.config.contains("game.party.size"))
            arenaConfig.config.set("game.party.size", -1);
        if(!arenaConfig.config.contains("game.party.damage"))
            arenaConfig.config.set("game.party.damage", true);

        if(!arenaConfig.config.contains("game.firework.death"))
            arenaConfig.config.set("game.firework.death", false);
        if(!arenaConfig.config.contains("game.firework.join"))
            arenaConfig.config.set("game.firework.join", true);
        if(!arenaConfig.config.contains("game.firework.leave"))
            arenaConfig.config.set("game.firework.leave", false);
        if(!arenaConfig.config.contains("game.firework.count"))
            arenaConfig.config.set("game.firework.count", 4);

        if(!arenaConfig.config.contains("game.lightning.death"))
            arenaConfig.config.set("game.lightning.death", true);
        if(!arenaConfig.config.contains("game.lightning.join"))
            arenaConfig.config.set("game.lightning.join", false);
        if(!arenaConfig.config.contains("game.lightning.leave"))
            arenaConfig.config.set("game.lightning.leave", true);
        if(!arenaConfig.config.contains("game.lightning.count"))
            arenaConfig.config.set("game.lightning.count", 2);

        arenaConfig.saveConfig();
    }

    /**
     * Completely deletes the arena
     */
    public void delete()
    {
        arenaConfig.configFile.delete();
    }

    /**
     * Indicates if the arena exists
     * @return If the arena exists or not
     */
    public boolean doesArenaExists()
    {
        return arenaConfig.exists();
    }

    /**
     * Changes a setting of the arena
     * @param name Path of the setting
     * @param value The new value
     */
    public void changeConfig(String name, Object value)
    {
        arenaConfig.config.set(name, value);
    }

    /**
     * Returns the configuration for the game-settings
     * @return Configuration for the game-settings
     */
    public ConfigurationSection getGameConfiguration()
    {
        return arenaConfig.config.getConfigurationSection("game");
    }

    /**
     * Changes the selection of the arena
     * @param weSelection The new WorldEdit-Selection
     */
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

    /**
     * Adds an new arena-spawn at the current location
     * @param loc The location of the new spawn
     * @return The number of the spawn
     */
    public int addSpawn(Location loc)
    {
        List<String> spawns = arenaConfig.config.getStringList("spawns");
        spawns.add(loc.getX() + ";" + loc.getY() + ";" + loc.getZ());

        arenaConfig.config.set("spawns", spawns);
        arenaConfig.saveConfig();

        return spawns.size();
    }

    /**
     * Removes the last spawn
     * @return The number of the removed spawn
     */
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

    /**
     * Sets a new spawn for the spectators
     * @param loc The location
     */
    public void setSpecSpawn(Location loc)
    {
        arenaConfig.config.set("arena.spectator.x", loc.getX());
        arenaConfig.config.set("arena.spectator.y", loc.getY());
        arenaConfig.config.set("arena.spectator.z", loc.getZ());
        arenaConfig.saveConfig();
    }

    /**
     * Adds the player to the player-list of the arena if the arena is not full
     * @param p The player
     * @return If the player can join the arena
     */
    public Integer addPlayer(Player p, Long partyID)
    {
        if(playerList.contains(p.getUniqueId().toString())) // player is already in list, e.g. party-join
            return 0;

        if(playerList.size() == getGameConfiguration().getInt("max-players"))
            return -1;

        if(isRankedArena())
        {
            if(rankedMatchRunning)
                return -10;
            else
            {
                queuePlayerList.add(p.getUniqueId().toString());
                return 1;
            }
        }

        if(getGameConfiguration().getBoolean("party.only"))
        {
            if(partyID == -1)
                return -2;
            else if(!PartyManager.instance.isPartyLeader(p))
                return -3;
            else if(getGameConfiguration().getInt("party.size") != -1)
            {
                Integer count = PartyManager.instance.getPartyMembers(p).size() + 1; // count of members + leader

                if(count > getGameConfiguration().getInt("party.size"))
                    return -4;
                else if(count < getGameConfiguration().getInt("party.size"))
                    return -5;
            }

            // here the party should be OK
            for(String member : PartyManager.instance.getPartyMembers(p))
                this.playerList.add(p.getUniqueId().toString());
        }

        if(!playerList.contains(p.getUniqueId().toString()))
            this.playerList.add(p.getUniqueId().toString());

        return 0;
    }

    /**
     * Removes the player from the player-list of the arena
     * @param p The player
     */
    public void removePlayer(Player p)
    {
        // run death-actions
        runAction("death", p.getLocation());

        if(playerList.contains(p.getUniqueId().toString()))
            this.playerList.remove(p.getUniqueId().toString());

        if(playerList.size() == 0 && isRankedArena() && !PvP.isDisabling)
            startQueue();
    }

    /**
     * Gets the random spawn-point and teleports the player
     * @param p The player
     */
    public void teleportPlayer(Player p, boolean deathTp)
    {
        // Load world
        World arenaworld = Bukkit.getServer().getWorld(arenaConfig.config.getString("arena.world"));

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

                // do death-actions if any is enabled
                if(deathTp)
                    runAction("death", p.getLocation());

                // activaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, Double.parseDouble(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2])));

                // deactivaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);

                // do join-actions if any is enabled
                runAction("join", new Location(arenaworld, Double.parseDouble(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2])));
            }
            else
            {
                // load points of arena
                double minX = arenaConfig.config.getDouble("arena.min.x");
                double minZ = arenaConfig.config.getDouble("arena.max.z");

                double maxX = arenaConfig.config.getDouble("arena.min.x");
                double maxZ = arenaConfig.config.getDouble("arena.max.z");

                // choose random spawn location
                // calculate the random spawn-point
                double x = Math.min(minX, maxX) + (double)Math.round(-0.5f + (1 + Math.abs(minX - maxX)) * Math.random());
                double z = Math.min(minZ, maxZ) + (double)Math.round(-0.5f + (1 + Math.abs(minZ - maxZ)) * Math.random());
                int y = arenaworld.getHighestBlockYAt((int)x, (int)z);

                // do death-actions if any is enabled
                if(deathTp)
                    runAction("death", p.getLocation());

                // activaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, x, y, z));

                // deactivaing teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);

                // do join-actions if any is enabled
                runAction("join", new Location(arenaworld, x, y, z));
            }
        }
        else
        {
            // world does not exists
            p.sendMessage(PvP.errorPrefix + MessageManager.instance.get(p, "commands.error.no-world", arenaConfig.config.getString("arena.world")));
        }
    }

    /**
     * Indicates if the arena is for ELO-Ranking
     * @return If arena is for ELO-Ranking
     */
    public boolean isRankedArena()
    {
        return getGameConfiguration().getInt("max-players") == 2 && getGameConfiguration().getBoolean("is-ranked");
    }

    /**
     * Indicates if the player is in the raking-queue
     * @param p The player which should be checked
     * @return If player is in queue
     */
    public boolean isPlayerInQueue(Player p)
    {
        return queuePlayerList.contains(p.getUniqueId().toString());
    }

    /**
     * Indicates if the player is in the raking-queue
     * @param p The player which would leave to queue
     * @return If player is in queue
     */
    public void leaveFromQueue(Player p)
    {
        queuePlayerList.remove(p.getUniqueId().toString());
    }

    /**
     * Returns a list with all players in the arena
     * @return List with players
     */
    public List<String> getPlayerList()
    {
        return Collections.unmodifiableList(this.playerList);
    }

    /**
     * Returns a list with all players in the arena-queue
     * @return List with queued players
     */
    public List<String> getQueuedPlayerList()
    {
        return Collections.unmodifiableList(this.queuePlayerList);
    }

    /**
     * Starts the task for queue-working
     */
    public void startQueue()
    {
        queueCounter = PvP.getInstance().getConfig().getInt("ingame.ranking.queue-duration");
        queueTaskId = PvP.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(PvP.getInstance(), new Runnable() {

            @Override
            public void run()
            {
                if(PvP.getInstance().getConfig().getBoolean("ingame.ranking.notify") &&
                        PvP.getInstance().getConfig().getIntegerList("ingame.ranking.notify-timings").contains(queueCounter))
                    notifyQueuedPlayers("ingame.ranking.queue-notify", queueCounter);

                if(PvP.getInstance().getConfig().getInt("ingame.ranking.selection-timing") == queueCounter)
                {
                    Integer randomPlayer = rand.nextInt(queuePlayerList.size());

                    Player p1 = PvP.getInstance().getServer().getPlayer(UUID.fromString(queuePlayerList.get(randomPlayer)));
                    Player p2 = null;

                    Integer p1_elo = InGameManager.instance.getPlayer(p1).getElo();
                    Integer p2_elo = 0;

                    Integer selectionDiff = 0;

                    queuePlayerList.remove(p1.getUniqueId().toString());

                    for(int i = 0; i < queuePlayerList.size(); i++)
                    {
                        p2 = PvP.getInstance().getServer().getPlayer(UUID.fromString(queuePlayerList.get(i)));
                        p2_elo = InGameManager.instance.getPlayer(p2).getElo();
                        Integer tmpDiff = 0;

                        if(p2_elo < p1_elo)
                            tmpDiff = p1_elo - p2_elo;
                        else if(p2_elo > p1_elo)
                            tmpDiff = p2_elo - p1_elo;
                        else
                            tmpDiff = 0;

                        if(tmpDiff < selectionDiff)
                            selectionDiff = tmpDiff;
                    }

                    for(int i = 0; i < queuePlayerList.size(); i++)
                    {
                        p2 = PvP.getInstance().getServer().getPlayer(UUID.fromString(queuePlayerList.get(i)));
                        p2_elo = InGameManager.instance.getPlayer(p2).getElo();

                        if(p2_elo == selectionDiff) // player selected
                            break;
                    }

                    notifyQueuedPlayers("ingame.ranking.match-selected", p1.getName(), p1_elo, p2.getName(), p2_elo);

                    queuePlayerList.clear();

                    joiningPlayerList.clear();
                    joiningPlayerList.add(p1.getUniqueId().toString());
                    joiningPlayerList.add(p2.getUniqueId().toString());
                }

                if(queueCounter <= 0)
                {
                    if(joiningPlayerList.size() < 2)
                        notifyQueuedPlayers("ingame.ranking.queue-not-enough-players");
                    else
                    {
                        for(String uuid : joiningPlayerList)
                        {
                            Player p = PvP.getInstance().getServer().getPlayer(UUID.fromString(uuid));

                            playerList.add(uuid);
                            InGameManager.instance.joinArena(p, getName());
                        }
                        PvP.getInstance().getServer().getScheduler().cancelTask(queueTaskId);
                    }
                }

                queueCounter--;
            }

        }, 0L, 20L);
    }

    private void notifyQueuedPlayers(String msg, Object... args)
    {
        for(String uuid : queuePlayerList)
        {
            Player p = PvP.getInstance().getServer().getPlayer(UUID.fromString(uuid));
            p.sendMessage(PvP.prefix + MessageManager.instance.get(p, msg, args));
        }
    }

    /**
     * Returns the lower location of the selection
     * @return The lower location
     */
    public Location getMinLocation()
    {
        World arenaworld = Bukkit.getServer().getWorld(arenaConfig.config.getString("arena.world"));

        if(arenaworld == null)
            return null;

        return new Location(arenaworld, arenaConfig.config.getDouble("arena.min.x"),
                arenaConfig.config.getDouble("arena.min.y"), arenaConfig.config.getDouble("arena.min.z"));
    }

    /**
     * Returns the higher location of the selection
     * @return The higher location
     */
    public Location getMaxLocation()
    {
        World arenaworld = Bukkit.getServer().getWorld(arenaConfig.config.getString("arena.world"));

        if(arenaworld == null)
            return null;

        return new Location(arenaworld, arenaConfig.config.getDouble("arena.max.x"),
                arenaConfig.config.getDouble("arena.max.y"), arenaConfig.config.getDouble("arena.max.z"));
    }

    /**
     * The name of the arena
     * @return Name of arena
     */
    public String getName()
    {
        return this.arenaName;
    }

    /**
     * Gets the random spawn-point and teleports the player
     * @param p The player
     */
    public void spectate(Player p)
    {
        World arenaworld = Bukkit.getServer().getWorld(arenaConfig.config.getString("arena.world"));

        // check if spectator-spawn is set
        if(arenaConfig.config.contains("arena.spectator"))
        {
            Location loc = new Location(arenaworld, arenaConfig.config.getDouble("arena.spectator.x"),
                    arenaConfig.config.getDouble("arena.spectator.y"), arenaConfig.config.getDouble("arena.spectator.z"));

            p.teleport(loc);
        }
        else
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

                // activating teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, Double.parseDouble(locationParts[0]), Double.parseDouble(locationParts[1]), Double.parseDouble(locationParts[2])));

                // deactivating teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);
            }
            else
            {
                // load points of arena
                double minX = arenaConfig.config.getDouble("arena.min.x");
                double minZ = arenaConfig.config.getDouble("arena.max.z");

                double maxX = arenaConfig.config.getDouble("arena.min.x");
                double maxZ = arenaConfig.config.getDouble("arena.max.z");

                // choose random spawn location
                // calculate the random spawn-point
                double x = Math.min(minX, maxX) + (double)Math.round(-0.5f + (1 + Math.abs(minX - maxX)) * Math.random());
                double z = Math.min(minZ, maxZ) + (double)Math.round(-0.5f + (1 + Math.abs(minZ - maxZ)) * Math.random());
                int y = arenaworld.getHighestBlockYAt((int)x, (int)z);

                // activating teleportation for player
                InGameManager.instance.changeTeleportStatus(p, true);

                // teleport the player
                p.teleport(new Location(arenaworld, x, y, z));

                // deactivating teleportation for player
                InGameManager.instance.changeTeleportStatus(p, false);
            }
        }
    }

    private void runAction(String mode, Location loc)
    {
        if(getGameConfiguration().getBoolean("firework." + mode) && !PvP.isDisabling)
        {
            PvP.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PvP.getInstance(), new Runnable() {

                @Override
                public void run()
                {
                    FireworkUtils.launchRandomFirework(loc, getGameConfiguration().getInt("firework.count"));
                }

            }, 5L);
        }

        if(getGameConfiguration().getBoolean("lightning." + mode) && !PvP.isDisabling)
        {
            PvP.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(PvP.getInstance(), new Runnable() {

                @Override
                public void run()
                {
                    for(int i = 0; i < getGameConfiguration().getInt("lightning.count"); i++)
                        loc.getWorld().strikeLightningEffect(loc);
                }

            }, 5L);
        }
    }

}
