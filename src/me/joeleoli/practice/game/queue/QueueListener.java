package me.joeleoli.practice.game.queue;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.queue.event.PlayerEnterQueueEvent;
import me.joeleoli.practice.game.queue.event.PlayerExitQueueEvent;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.GameUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QueueListener implements Listener {

    public QueueListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    @EventHandler
    public void onEnterQueue(PlayerEnterQueueEvent event) {
        GameUtils.resetPlayer(event.getPlayer());

        event.getPlayer().getInventory().setContents(GameUtils.getQueueInventory());
        event.getPlayer().updateInventory();
    }

    @EventHandler
    public void onExitQueue(PlayerExitQueueEvent event) {
        Player player = event.getPlayer();
        PracticeProfile playerData = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        event.getQueue().removeFromQueue(playerData.getQueueData());
    }

}