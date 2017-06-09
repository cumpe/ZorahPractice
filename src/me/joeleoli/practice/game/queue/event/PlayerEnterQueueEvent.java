package me.joeleoli.practice.game.queue.event;

import me.joeleoli.practice.game.queue.IQueue;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

public class PlayerEnterQueueEvent extends PlayerQueueEvent implements Cancellable {

    private static HandlerList handlers = new HandlerList();;
    private boolean cancelled;
    
    public PlayerEnterQueueEvent(Player player, IQueue queue) {
        super(player, queue);
        this.cancelled = false;
    }
    
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    
    @Override
    public HandlerList getHandlers() {
        return PlayerEnterQueueEvent.handlers;
    }
    
    public static HandlerList getHandlerList() {
        return PlayerEnterQueueEvent.handlers;
    }

}
