package me.joeleoli.practice.game.party.event;

import me.joeleoli.practice.game.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerKickPlayerPartyEvent extends PlayerPartyEvent implements Cancellable {

    private boolean cancelled;
    private Player kicked;
    private boolean clean;
    private boolean announce;
    
    public PlayerKickPlayerPartyEvent(Player player, Player kicked, Party party, boolean clean, boolean announce) {
        super(player, party);
        this.cancelled = false;
        this.clean = false;
        this.announce = false;
        this.kicked = kicked;
        this.clean = clean;
        this.announce = announce;
    }
    
    public Player getKickedPlayer() {
        return this.kicked;
    }
    
    public boolean shouldClean() {
        return this.clean;
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
