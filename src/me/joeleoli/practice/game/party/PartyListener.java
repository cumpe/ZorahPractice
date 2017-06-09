package me.joeleoli.practice.game.party;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.party.event.*;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.PlayerBoard;
import me.joeleoli.practice.util.GameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;

public class PartyListener implements Listener {

    public PartyListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }
    
    @EventHandler(priority=EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getParty() == null) {
            return;
        }

        if (profile.getParty().getLeaderUuid().equals(player.getUniqueId())) {
            PlayerDisbandPartyEvent partyEvent = new PlayerDisbandPartyEvent(player, profile.getParty());
            Bukkit.getPluginManager().callEvent(partyEvent);
        }
        else {
            PlayerLeavePartyEvent partyEvent = new PlayerLeavePartyEvent(player, profile.getParty(), false, true);
            Bukkit.getPluginManager().callEvent(partyEvent);
        }
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getParty() == null) {
            return;
        }

        if (profile.getParty().getLeaderUuid().equals(player.getUniqueId())) {
            PlayerDisbandPartyEvent partyEvent = new PlayerDisbandPartyEvent(player, profile.getParty());
            Bukkit.getPluginManager().callEvent(partyEvent);
        }
        else {
            PlayerLeavePartyEvent partyEvent = new PlayerLeavePartyEvent(player, profile.getParty(), false, true);
            Bukkit.getPluginManager().callEvent(partyEvent);
        }
    }

    @EventHandler
    public void onCreateParty(PlayerCreatePartyEvent event) {
        Player player = event.getPlayer();
        ManagerHandler.getPartyManager().addParty(event.getParty());

        GameUtils.resetPlayer(player);

        player.getInventory().setContents(GameUtils.getPartyLeaderInventory());
        player.updateInventory();
        player.sendMessage(ChatColor.GRAY + "You have created a new party.");
    }

    @EventHandler
    public void onDisbandParty(PlayerDisbandPartyEvent event) {
        Party party = event.getParty();

        ManagerHandler.getPartyManager().removeParty(party);
        party.sendMessage(ChatColor.GRAY + "The party has been disbanded by " + ChatColor.AQUA + party.getLeader().getName());

        List<Player> players = party.getPlayers();

        for (Player player : players) {
            ManagerHandler.getPlayerManager().getPlayerProfile(player).setParty(null);

            GameUtils.resetPlayer(player);

            player.getInventory().setContents(GameUtils.getLobbyInventory());
            player.updateInventory();

            PlayerBoard board = ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId());

            if(board != null) {
                board.addUpdates(Bukkit.getOnlinePlayers());
            }
        }

        event.getPlayer().sendMessage(ChatColor.GRAY + "You have disbanded the party.");
    }

    @EventHandler
    public void onJoinParty(PlayerJoinPartyEvent event) {
        Party party = event.getParty();
    	Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
    	
    	profile.setParty(party);
    	
        GameUtils.resetPlayer(player);

        player.getInventory().setContents(GameUtils.getPartyMemberInventory());
        player.updateInventory();

        party.removeInvite(player.getUniqueId());
        party.addPlayer(player);
        party.sendMessage(ChatColor.AQUA + event.getPlayer().getName() + ChatColor.GRAY + " has joined the party.");

        List<Player> players = party.getPlayers();

        for (Player p : players) {
            PlayerBoard board = ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId());

            if(board != null) {
                board.addUpdate(event.getPlayer());
            }
        }

        ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(event.getParty().getPlayers());
    }

    @EventHandler
    public void onLeaveParty(PlayerLeavePartyEvent event) {
        Party party = event.getParty();
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

    	if (party.getStatus() == PartyStatus.QUEUING) {
            profile.getQueueData().getQueue().removeFromQueue(profile.getQueueData());
        }

        profile.setParty(null);
        party.removePlayer(player);

        if (event.shouldClean()) {
            GameUtils.resetPlayer(player);

            player.getInventory().setContents(GameUtils.getLobbyInventory());
            player.updateInventory();
            ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(party.getPlayers());
        }

        if (event.shouldAnnounce()) {
            party.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GRAY + " has left the party.");
        }

        List<Player> players = party.getPlayers();

        for (Player p : players) {
            PlayerBoard board = ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId());

            if(board != null) {
                board.addUpdate(player);
            }
        }
    }

    @EventHandler
    public void onKickPlayer(PlayerKickPlayerPartyEvent event) {
        Party party = event.getParty();
        Player player = event.getPlayer();
        Player kicked = event.getKickedPlayer();

    	ManagerHandler.getPlayerManager().getPlayerProfile(kicked).setParty(null);

        party.removePlayer(kicked);

        if (event.shouldClean()) {
            GameUtils.resetPlayer(kicked);
            kicked.getInventory().setContents(GameUtils.getLobbyInventory());
            kicked.updateInventory();
        }

        if (event.shouldAnnounce()) {
            party.sendMessage(ChatColor.AQUA + kicked.getName() + ChatColor.GRAY + " has been kicked from the party by " + ChatColor.AQUA + player.getName() + ChatColor.GRAY + ".");
        }

        List<Player> players = party.getPlayers();

        for (Player p : players) {
            PlayerBoard board = ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId());

            if(board != null) {
                board.addUpdate(kicked);
            }
        }
    }

}