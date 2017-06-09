package me.joeleoli.practice.game.party.event;

import me.joeleoli.practice.game.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerJoinPartyEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    private boolean announce;
    
    public PlayerJoinPartyEvent(Player player, Party party, boolean announce) {
        super(player, party);
        this.cancelled = false;
        this.announce = false;
        this.announce = announce;
    }
    
    public boolean shouldAnnounce() {
        return this.announce;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
