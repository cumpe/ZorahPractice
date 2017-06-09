package me.joeleoli.practice.game.match;

import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.ladder.Ladder;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface IMatch {

    UUID getIdentifier();
    
    Ladder getLadder();
    
    Arena getArena();
    
    MatchStatus getMatchStatus();
    
    MatchType getMatchType();

    List<Player> getPlayers();

    List<UUID> getSpectators();

    List<Player> getTeam(Player player);

    List<Player> getOpponents(Player player);

    Timestamp getStartTimestamp();

    Long getStartNano();

    int getOpponentsLeft(Player player);

    void sendMessage(String message);
    
    void handleDeath(Player player, Location location, String deathMessage);

    boolean isDead(Player player);
    
    void cancelMatch(String reason);

}