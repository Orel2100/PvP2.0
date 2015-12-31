package at.lukasberger.bukkit.pvp.core;

import at.lukasberger.bukkit.pvp.PvP;
import at.lukasberger.bukkit.pvp.utils.MapTuple;
import at.lukasberger.bukkit.pvp.utils.MapTupleUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * PvP 2.0, Copyright (c) 2015 Lukas Berger, licensed under GPLv3
 */
public class InviteManager
{

    // lists
    private List<MapTuple<String, String>> invites = new ArrayList<>();

    // instance
    public static InviteManager instance = new InviteManager();

    // disallow creation of other instances
    private InviteManager() { }

    // removes all invites
    public void removeAll()
    {
        invites.clear();
    }

    // sends an invitation
    public void invite(Player inviter, String invitedName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player invited = PvP.getInstance().getServer().getPlayer(invitedName);

        // check if inviting player is ingame
        if(!InGameManager.instance.isPlayerIngame(inviter))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.not-ingame"));
            return;
        }

        // check if invited player is online
        if(invited == null || !invited.isOnline())
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.not-online"));
            return;
        }

        // check if invited player is ingame
        if(InGameManager.instance.isPlayerIngame(invited))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.invited-ingame"));
            return;
        }

        // check if the inviter already invited the player
        if(tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) &&
               tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.already-invited", invited.getName()));
            return;
        }

        // check if the invited player already invited the inviter (^^)
        if(tupleUtils.containsKey(invites, invited.getUniqueId().toString()) &&
                tupleUtils.containsTuple(invites, invited.getUniqueId().toString(), inviter.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.other-invited", invited.getName()));
            return;
        }

        // send basic messages
        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get(inviter, "action.invite.sent", invited.getName()));
        invited.sendMessage(PvP.warningPrefix + MessageManager.instance.get(invited, "action.invite.invited", inviter.getName()));

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

    // accept the invitation
    public void accept(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        // check if inviter is still online
        if(inviter == null || !inviter.isOnline())
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get(invited, "action.party.invite.not-online-anymore"));
            return;
        }

        // check if invited player was invited
        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get(invited, "action.invite.not-invited", inviter.getName()));
            return;
        }

        // teleport invited to arena
        String arena = InGameManager.instance.getArena(inviter);
        InGameManager.instance.joinArena(invited, arena);

        // remove invitation from invites-list
        invites = tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());

        // send messages
        invited.sendMessage(PvP.successPrefix + MessageManager.instance.get(invited, "action.invite.joining", inviter.getName()));
        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get(inviter, "action.invite.joined", invited.getName()));
    }

    // denies the invitation
    public void deny(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        // check if inviter is still online
        if(inviter == null || !inviter.isOnline())
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get(invited, "action.party.invite.not-online-anymore"));
            return;
        }

        // check if invited player was invited
        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get(invited, "action.invite.not-invited", inviter.getName()));
            return;
        }

        // remove invitation from invites-list
        invites = tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());

        // send messages
        invited.sendMessage(PvP.successPrefix + MessageManager.instance.get(invited, "action.invite.denying", inviter.getName()));
        inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get(inviter, "action.invite.denied", invited.getName()));
    }

}
