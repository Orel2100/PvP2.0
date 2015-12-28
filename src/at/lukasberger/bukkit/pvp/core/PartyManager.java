package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.core.messages.MessageManager;
import at.lukasberger.bukkit.pvp.utils.MapTuple;
import at.lukasberger.bukkit.pvp.utils.MapTupleUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class PartyManager
{

    // lists
    private List<Long> partyIds = new ArrayList<>();
    private HashMap<String, Long> leaderToParty = new HashMap<>();
    private HashMap<Long, String> partyToLeader = new HashMap<>();
    private HashMap<String, Long> membersToParty = new HashMap<>();
    private List<MapTuple<String, String>> invites = new ArrayList<>();

    private Long staticCounter = 0L;

    // instance
    public static PartyManager instance = new PartyManager();

    // disallow creation of other instances
    private PartyManager() { }

    public void create(Player leader)
    {
        partyIds.add(staticCounter);
        leaderToParty.put(leader.getUniqueId().toString(), staticCounter);
        partyToLeader.put(staticCounter, leader.getUniqueId().toString());

        leader.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.party.created"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-follow"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-invite"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-leader"));

        staticCounter++;
    }

    public void delete(Player leader)
    {
        Long leaderPartyID = getPartyID(leader);

        if(leaderPartyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-in-party"));
            return;
        }

        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-leader"));
            return;
        }

        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == leaderPartyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                membersToParty.remove(memberEntry.getKey());
                member.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.party.deleted-member", leader.getName()));
            }
        }

        partyIds.remove(leaderPartyID);
        leaderToParty.remove(leader.getUniqueId().toString());
        partyToLeader.remove(leaderPartyID);

        leader.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.party.deleted-leader"));
    }

    public void changeLeader(Player currLeader, String newLeaderName)
    {
        Player newLeader = PvP.getInstance().getServer().getPlayer(newLeaderName);

        Long currLeaderPartyID = getPartyID(currLeader);
        Long newLeaderPartyID = getPartyID(newLeader);

        if(currLeaderPartyID == -1)
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-in-party"));
            return;
        }

        if(!isPartyLeader(currLeader))
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-leader"));
            return;
        }

        if(newLeaderPartyID == -1 || newLeaderPartyID != currLeaderPartyID)
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.player-not-in-party", newLeader.getName()));
            return;
        }

        if(leaderToParty.containsKey(currLeader.getUniqueId().toString()))
            leaderToParty.remove(currLeader.getUniqueId().toString());

        if(partyToLeader.containsKey(currLeaderPartyID))
            partyToLeader.remove(currLeaderPartyID);

        leaderToParty.put(newLeader.getUniqueId().toString(), currLeaderPartyID);
        partyToLeader.put(currLeaderPartyID, newLeader.getUniqueId().toString());

        currLeader.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.party.leader-old", newLeader.getName()));
        newLeader.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.party.leader-new", currLeader.getName()));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-follow"));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-invite"));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get("action.party.created-help-leader"));
    }

    public void invite(Player inviter, String invitedName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player invited = PvP.getInstance().getServer().getPlayer(invitedName);

        if(invited == null || !invited.isOnline())
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.not-online"));
            return;
        }

        if(tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) &&
                tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.already-invited", invited.getName()));
            return;
        }

        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.party.invite.sent", invited.getName()));
        invited.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.party.invite.invited", inviter.getName()));

        invited.spigot().sendMessage(TextComponent.fromLegacyText("{\"text\":\"" + MessageManager.instance.get("action.click.invite-accept") +
                "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"pvp accept " + inviter.getName() + "\"}}"));
        invited.spigot().sendMessage(TextComponent.fromLegacyText("{\"text\":\"" + MessageManager.instance.get("action.click.invite-deny") +
                "\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"pvp deny " + inviter.getName() + "\"}}"));

        invites.add(new MapTuple<String, String>(inviter.getUniqueId().toString(), invited.getUniqueId().toString()));
    }

    public void accept(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.not-invited", inviter.getName()));
            return;
        }

        Long partyID = leaderToParty.get(inviter.getUniqueId().toString());

        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == partyID)
            {
                Player member_ = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));
                member_.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.accept-announce", invited.getName()));
            }
        }

        membersToParty.put(invited.getUniqueId().toString(), partyID);
        invited.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.accept", inviter.getName()));
        inviter.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.accept-announce", invited.getName()));

        tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());
    }

    public void deny(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.not-invited", inviter.getName()));
            return;
        }

        invited.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.denied", inviter.getName()));
        inviter.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.denied-leader", invited.getName()));

        tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());
    }

    public void leave(Player member)
    {
        Long partyID = membersToParty.get(member.getUniqueId().toString());

        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == partyID)
            {
                Player member_ = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));
                member_.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.leave-announce", member.getName()));
            }
        }

        membersToParty.remove(member.getUniqueId().toString());
        member.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.leave"));
    }

    public void memberMassJoin(Player leader, String arena)
    {
        Long partyID = leaderToParty.get(leader.getUniqueId().toString());

        if(partyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-in-party"));
            return;
        }

        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-leader"));
            return;
        }

        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == partyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                InGameManager.instance.joinArena(member, arena);
                member.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.joined-game", arena));
            }
        }
    }

    public void memberMassLeave(Player leader)
    {
        Long partyID = leaderToParty.get(leader.getUniqueId().toString());

        if(partyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-in-party"));
            return;
        }

        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.error.not-leader"));
            return;
        }

        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == partyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                InGameManager.instance.leaveArena(member);
                member.sendMessage(PvP.prefix + MessageManager.instance.get("action.party.left-game"));
            }
        }
    }

    public Long getPartyID(Player p)
    {
        if(!isPlayerInAnyParty(p) && !isPartyLeader(p))
            return -1L;

        return membersToParty.get(p.getUniqueId().toString());
    }

    public boolean isPartyLeader(Player p)
    {
        return leaderToParty.containsKey(p.getUniqueId().toString());
    }

    public boolean isPlayerInAnyParty(Player p)
    {
        return membersToParty.containsKey(p.getUniqueId().toString());
    }

    public boolean isPlayerInParty(Player p, Long id)
    {
        return membersToParty.containsKey(p.getUniqueId().toString()) && membersToParty.get(p.getUniqueId().toString()) == id;
    }

}
