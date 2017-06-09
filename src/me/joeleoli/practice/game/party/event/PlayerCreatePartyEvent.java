package me.joeleoli.practice.game.party.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerCreatePartyEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    
    public PlayerCreatePartyEvent(Player player) {
        super(player, null);
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}