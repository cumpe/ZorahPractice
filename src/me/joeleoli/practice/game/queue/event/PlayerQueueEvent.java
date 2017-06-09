package me.joeleoli.practice.game.queue.event;

import lombok.Getter;
import me.joeleoli.practice.game.queue.IQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQueueEvent extends Event {

    @Getter private IQueue queue;
    @Getter private Player player;
    
    public PlayerQueueEvent(Player player, IQueue queue) {
        this.queue = queue;
        this.player = player;
    }
    
    public HandlerList getHandlers() {
        return null;
    }

}