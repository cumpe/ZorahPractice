package me.joeleoli.practice.game.party;

import lombok.Getter;
import lombok.Setter;

import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.IMatch;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {

    @Getter private UUID identifier;
    @Getter private UUID leaderUuid;
    @Getter @Setter private PartyStatus status;
    @Getter @Setter private IMatch match;
    private List<UUID> players = new ArrayList<>();
    private List<UUID> invites = new ArrayList<>();
    private Map<UUID, Ladder> requests = new HashMap<>();
    
    public Party(Player leader) {
        this.identifier = UUID.randomUUID();
        this.leaderUuid = leader.getUniqueId();
        this.status = PartyStatus.IDLE;
        this.players.add(leader.getUniqueId());
    }

    public Player getLeader() {
        return Bukkit.getPlayer(this.leaderUuid);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        Iterator<UUID> iterator = this.players.iterator();

        while(iterator.hasNext()) {
            UUID uuid = iterator.next();
            Player p = Bukkit.getPlayer(uuid);

            if(p == null || !p.isOnline()) {
                iterator.remove();
            }
            else {
                players.add(p);
            }
        }

        return players;
    }
    
    public void sendMessage(String message) {
        for (Player player : this.getPlayers()) {
            player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "Party" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + message);
        }
    }

    public boolean hasInvite(UUID uuid) {
        return this.invites.contains(uuid);
    }

    public void addInvite(UUID uuid) {
        this.invites.add(uuid);
    }

    public void removeInvite(UUID uuid) {
        this.invites.remove(uuid);
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.contains(uuid);
    }

    void addPlayer(Player player) {
        this.players.add(player.getUniqueId());
    }

    void removePlayer(Player player) {
        this.players.remove(player.getUniqueId());
    }

    public boolean hasRequest(Party party) {
        return this.requests.containsKey(party.getIdentifier());
    }

    public void addRequest(Party sender, Ladder ladder) {
        new FancyMessage(
                "You have been sent a party duel request by ").color(ChatColor.GRAY)
                .then(sender.getLeader().getName()).color(ChatColor.AQUA)
                .then(" [Click to Accept]").color(ChatColor.GREEN).command("/duel accept " + sender.getLeader().getName())
                .send(this.getLeader());

        sender.getLeader().sendMessage(ChatColor.GRAY + "You have sent " + ChatColor.AQUA + this.getLeader().getName() + ChatColor.GRAY + " a party duel request.");
        this.requests.put(sender.getIdentifier(), ladder);
    }

    public Ladder getRequest(Party sender) {
        return this.requests.get(sender.getIdentifier());
    }

    public void removeRequest(Party party) {
        this.requests.remove(party.getIdentifier());
    }

}