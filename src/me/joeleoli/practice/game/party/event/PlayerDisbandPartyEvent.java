package me.joeleoli.practice.game.party.event;

import me.joeleoli.practice.game.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerDisbandPartyEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    
    public PlayerDisbandPartyEvent(Player player, Party party) {
        super(player, party);
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}