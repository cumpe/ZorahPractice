package me.joeleoli.practice.game.party.event;

import lombok.Getter;
import lombok.Setter;
import me.joeleoli.practice.game.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PlayerPartyEvent extends Event {

    private static HandlerList handlers = new HandlerList();
    @Getter @Setter private Party party;
    @Getter private Player player;
    
    public PlayerPartyEvent(Player player, Party party) {
        this.party = party;
        this.player = player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}