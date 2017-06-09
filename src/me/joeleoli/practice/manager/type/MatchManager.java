package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.game.match.IMatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MatchManager {
    
    @Getter private Map<UUID, IMatch> matches;
    
    public MatchManager() {
        this.matches = new HashMap<>();
    }

    public void cancelMatches() {
        for (IMatch match : matches.values()) {
            match.cancelMatch("RELOAD");
        }
    }

}