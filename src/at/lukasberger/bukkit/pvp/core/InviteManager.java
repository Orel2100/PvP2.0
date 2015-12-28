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

    public void invite(Player inviter, String invitedName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player invited = PvP.getInstance().getServer().getPlayer(invitedName);

        if(!InGameManager.instance.isPlayerIngame(inviter))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.not-ingame"));
            return;
        }

        if(invited == null || !invited.isOnline())
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.not-online"));
            return;
        }

        if(InGameManager.instance.isPlayerIngame(invited))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.invited-ingame"));
            return;
        }

        if(tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) &&
               tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.already-invited", invited.getName()));
            return;
        }

        if(tupleUtils.containsKey(invites, invited.getUniqueId().toString()) &&
                tupleUtils.containsTuple(invites, invited.getUniqueId().toString(), inviter.getUniqueId().toString()))
        {
            inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.other-invited", invited.getName()));
            return;
        }

        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.invite.sent", invited.getName()));
        invited.sendMessage(PvP.warningPrefix + MessageManager.instance.get("action.invite.invited", inviter.getName()));

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

        invited.spigot().sendMessage(inviteAcceptComponent);
        invited.spigot().sendMessage(inviteDenyComponent);

        invites.add(new MapTuple<String, String>(inviter.getUniqueId().toString(), invited.getUniqueId().toString()));
    }

    public void accept(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        if(inviter == null || !inviter.isOnline())
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.not-online-anymore"));
            return;
        }

        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.not-invited", inviter.getName()));
            return;
        }

        String arena = InGameManager.instance.getArena(inviter);
        InGameManager.instance.joinArena(invited, arena);

        invites = tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());

        invited.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.invite.joining", inviter.getName()));
        inviter.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.invite.joined", invited.getName()));
    }

    public void deny(Player invited, String inviterName)
    {
        MapTupleUtils<String, String> tupleUtils = new MapTupleUtils<>();
        Player inviter = PvP.getInstance().getServer().getPlayer(inviterName);

        if(inviter == null || !inviter.isOnline())
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.party.invite.not-online-anymore"));
            return;
        }

        if(!tupleUtils.containsKey(invites, inviter.getUniqueId().toString()) || !tupleUtils.containsTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString()))
        {
            invited.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.not-invited", inviter.getName()));
            return;
        }

        invites = tupleUtils.removeTuple(invites, inviter.getUniqueId().toString(), invited.getUniqueId().toString());

        invited.sendMessage(PvP.successPrefix + MessageManager.instance.get("action.invite.denying", inviter.getName()));
        inviter.sendMessage(PvP.errorPrefix + MessageManager.instance.get("action.invite.denied", invited.getName()));
    }

}
