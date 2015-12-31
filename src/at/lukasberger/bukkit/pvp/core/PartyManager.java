package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.utils.MapTuple;
import at.lukasberger.bukkit.pvp.utils.MapTupleUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
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
        // fill lists
        partyIds.add(staticCounter);
        leaderToParty.put(leader.getUniqueId().toString(), staticCounter);
        partyToLeader.put(staticCounter, leader.getUniqueId().toString());

        // send messages
        leader.sendMessage(PvP.successPrefix + MessageManager.instance.get(leader, "action.party.created"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(leader, "action.party.created-help-follow"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(leader, "action.party.created-help-invite"));
        leader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(leader, "action.party.created-help-leader"));

        // non-decrementing id-counter
        staticCounter++;
    }

    // deletes the party of the given player
    public void delete(Player leader)
    {
        // get the id of the party
        Long leaderPartyID = getPartyID(leader);

        // check if the player is in a party
        if(leaderPartyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-in-party"));
            return;
        }

        // check if the player is the leader
        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-leader"));
            return;
        }

        // go through all members
        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            // check if the player is in this party
            if(memberEntry.getValue() == leaderPartyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                // remove player from party
                membersToParty.remove(memberEntry.getKey());
                // send message
                member.sendMessage(PvP.warningPrefix + MessageManager.instance.get(member, "action.party.deleted-member", leader.getName()));
            }
        }

        // completely remove parts
        partyIds.remove(leaderPartyID);
        leaderToParty.remove(leader.getUniqueId().toString());
        partyToLeader.remove(leaderPartyID);

        leader.sendMessage(PvP.successPrefix + MessageManager.instance.get(leader, "action.party.deleted-leader"));
    }

    // changes the leader of the party of the given player
    public void changeLeader(Player currLeader, String newLeaderName)
    {
        Player newLeader = PvP.getInstance().getServer().getPlayer(newLeaderName);

        // get the id of the party of both
        Long currLeaderPartyID = getPartyID(currLeader);
        Long newLeaderPartyID = getPartyID(newLeader);

        // check if the player is in a party
        if(currLeaderPartyID == -1)
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(currLeader, "action.party.error.not-in-party"));
            return;
        }

        // check if the player is the leader
        if(!isPartyLeader(currLeader))
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(currLeader, "action.party.error.not-leader"));
            return;
        }

        // check if the new leader is in the same party
        if(newLeaderPartyID == -1 || newLeaderPartyID != currLeaderPartyID)
        {
            currLeader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(currLeader, "action.party.error.player-not-in-party", newLeader.getName()));
            return;
        }

        // remove current leader
        if(leaderToParty.containsKey(currLeader.getUniqueId().toString()))
            leaderToParty.remove(currLeader.getUniqueId().toString());

        if(partyToLeader.containsKey(currLeaderPartyID))
            partyToLeader.remove(currLeaderPartyID);

        // add new leader
        leaderToParty.put(newLeader.getUniqueId().toString(), currLeaderPartyID);
        partyToLeader.put(currLeaderPartyID, newLeader.getUniqueId().toString());

        // send info-messages to old leader
        currLeader.sendMessage(PvP.successPrefix + MessageManager.instance.get(currLeader, "action.party.leader-old", newLeader.getName()));

        // send info-messages to new leader
        newLeader.sendMessage(PvP.successPrefix + MessageManager.instance.get(newLeader, "action.party.leader-new", currLeader.getName()));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(newLeader, "action.party.created-help-follow"));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(newLeader, "action.party.created-help-invite"));
        newLeader.sendMessage(ChatColor.GREEN + MessageManager.instance.get(newLeader, "action.party.created-help-leader"));
    }

    // invites the given player to the party of the leader
    public void invite(Player inviter, String invitedName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player invited = PvP.getInstance().getServer().getPlayer(invitedName);

        // check if the player is online
        if(invited == null || !invited.isOnline())
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.party.invite.not-online"));
            return;
        }

        // check if the invited is already online
        if(tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) &&
                tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.party.invite.already-invited", invited.getName()));
            return;
        }

        // checks if the player is already in a party
        if(membersToParty.containsKey(invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.party.invite.already-in-party", invited.getName()));
            return;
        }

        // send basic messages
        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get(inviter, "action.party.invite.sent", invited.getName()));
        invited.sendMessage(PvP.warningPrefix + MessageManager.instance.get(invited, "action.party.invite.invited", inviter.getName()));

        // create interactive messages using Spigot Chat Components
        TextComponent inviteAcceptComponent = new TextComponent();
        inviteAcceptComponent.setText("/pvp accept " + inviter.getName());
        inviteAcceptComponent.setColor(ChatColor.GREEN);
        inviteAcceptComponent.setBold(true);
        inviteAcceptComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp accept " + inviter.getName()));

        TextComponent inviteDenyComponent = new TextComponent();
        inviteDenyComponent.setText("/pvp deny " + inviter.getName());
        inviteDenyComponent.setColor(ChatColor.RED);
        inviteDenyComponent.setBold(true);
        inviteDenyComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pvp deny " + inviter.getName()));

        // send interactive messages
        invited.spigot().sendMessage(inviteAcceptComponent);
        invited.spigot().sendMessage(inviteDenyComponent);

        // add to invites-list
        invites.add(new MapTuple<String, String>(inviter.getUniqueId().toString(), invited.getUniqueId().toString()));
    }

    // accepts a invitation to a party
    public void accept(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        // check if the player is invited
        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.party.invite.not-invited", inviter.getName()));
            return;
        }

        // get party-id
        Long partyID = leaderToParty.get(inviter.getUniqueId().toString());

        // go through all members
        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            if(memberEntry.getValue() == partyID)
            {
                Player member_ = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                // notify members about the new player
                member_.sendMessage(PvP.prefix + MessageManager.instance.get(member_, "action.party.accept-announce", invited.getName()));
            }
        }

        // send basic messages
        invited.sendMessage(PvP.prefix + MessageManager.instance.get(invited, "action.party.accept", inviter.getName()));
        inviter.sendMessage(PvP.prefix + MessageManager.instance.get(inviter, "action.party.accept-announce", invited.getName()));

        // update lists
        membersToParty.put(invited.getUniqueId().toString(), partyID);
        tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());
    }

    // denies a invitation to a party
    public void deny(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.party.invite.not-invited", inviter.getName()));
            return;
        }

        // send basic messages
        invited.sendMessage(PvP.prefix + MessageManager.instance.get(invited, "action.party.denied", inviter.getName()));
        inviter.sendMessage(PvP.prefix + MessageManager.instance.get(inviter, "action.party.denied-leader", invited.getName()));

        // update lists
        tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());
    }

    // leaves the party
    public void leave(Player member)
    {
        // get party-id
        Long partyID = membersToParty.get(member.getUniqueId().toString());

        // go through all members
        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            // check if the player is in this party
            if(memberEntry.getValue() == partyID)
            {
                Player member_ = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                // notify members about leaving of the member
                member_.sendMessage(PvP.prefix + MessageManager.instance.get(member_, "action.party.leave-announce", member.getName()));
            }
        }

        // send basic messages
        member.sendMessage(PvP.prefix + MessageManager.instance.get(member, "action.party.leave"));

        // update lists
        membersToParty.remove(member.getUniqueId().toString());
    }

    // teleports all members of a party to the given arena
    public void memberMassJoin(Player leader, String arena)
    {
        // get party-id
        Long partyID = getPartyID(leader);

        // check if the player is in a party
        if(partyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-in-party"));
            return;
        }

        // check if the player is the leader
        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-leader"));
            return;
        }

        // go through all members
        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            // check if the player is in this party
            if(memberEntry.getValue() == partyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                // send the player to the arena
                InGameManager.instance.joinArena(member, arena);
                member.sendMessage(PvP.prefix + MessageManager.instance.get(member, "action.party.joined-game", arena));
            }
        }
    }

    // throws all members of a party from the PvP-arena
    public void memberMassLeave(Player leader)
    {
        // get party-id
        Long partyID = getPartyID(leader);

        // check if the player is in a party
        if(partyID == -1)
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-in-party"));
            return;
        }

        // check if the player is the leader
        if(!isPartyLeader(leader))
        {
            leader.sendMessage(PvP.errorPrefix + MessageManager.instance.get(leader, "action.party.error.not-leader"));
            return;
        }

        // go through all members
        for (Map.Entry<String, Long> memberEntry : membersToParty.entrySet())
        {
            // check if the player is in this party
            if(memberEntry.getValue() == partyID)
            {
                Player member = PvP.getInstance().getServer().getPlayer(UUID.fromString(memberEntry.getKey()));

                // throw player out of the arena
                InGameManager.instance.leaveArena(member);
                member.sendMessage(PvP.prefix + MessageManager.instance.get(member, "action.party.left-game"));
            }
        }
    }

    // returns the ID of a player's party, -1 if player is not in a party
    public Long getPartyID(Player p)
    {
        if(!isPlayerInAnyParty(p) && !isPartyLeader(p))
            return -1L;

        if(isPartyLeader(p))
            return leaderToParty.get(p.getUniqueId().toString());
        else
            return membersToParty.get(p.getUniqueId().toString());
    }

    // indicates if the player is the leader of his current party
    public boolean isPartyLeader(Player p)
    {
        return leaderToParty.containsKey(p.getUniqueId().toString());
    }

    // indicates if the player is in any parts
    public boolean isPlayerInAnyParty(Player p)
    {
        return membersToParty.containsKey(p.getUniqueId().toString());
    }

    // indicates if the player is in the given party
    public boolean isPlayerInParty(Player p, Long id)
    {
        return membersToParty.containsKey(p.getUniqueId().toString()) && membersToParty.get(p.getUniqueId().toString()) == id;
    }

    // removes all parties, invites etc.
    public void removeAll()
    {
        for(String playerName : membersToParty.keySet())
            leave(PvP.getInstance().getServer().getPlayer(UUID.fromString(playerName)));

        for(String playerName : leaderToParty.keySet())
            leave(PvP.getInstance().getServer().getPlayer(UUID.fromString(playerName)));

        membersToParty.clear();
        leaderToParty.clear();
        partyToLeader.clear();
        partyIds.clear();
        invites.clear();

        staticCounter = 0L;
    }

}
