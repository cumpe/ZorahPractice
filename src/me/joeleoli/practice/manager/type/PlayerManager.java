package me.joeleoli.practice.manager.type;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager implements Listener {

    @Getter private Map<UUID, PracticeProfile> profiles;

    public PlayerManager() {
        this.profiles = new HashMap<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setMaximumNoDamageTicks(19);
            PracticeProfile data = new PracticeProfile(player);
            this.profiles.put(player.getUniqueId(), data);
        }

        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    public Map<UUID, PracticeProfile> getAllData() {
        return this.profiles;
    }

    public PracticeProfile getPlayerProfile(Player player) {
        if (!this.profiles.containsKey(player.getUniqueId())) {
            this.profiles.put(player.getUniqueId(), new PracticeProfile(player));
        }

        return this.profiles.get(player.getUniqueId());
    }

    public PracticeProfile getPlayerProfile(UUID uuid) {
        return this.profiles.get(uuid);
    }

    public void saveData() {
        for (PracticeProfile profile : this.profiles.values()) {
            profile.save(null);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PracticeProfile playerData = new PracticeProfile(player);
        this.profiles.put(player.getUniqueId(), playerData);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (this.profiles.containsKey(player.getUniqueId())) {
            this.profiles.get(player.getUniqueId()).save(new GenericCallback() {
                @Override
                public void call(boolean result) {
                    profiles.remove(player.getUniqueId());
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (this.profiles.containsKey(player.getUniqueId())) {
            this.profiles.get(player.getUniqueId()).save(new GenericCallback() {
                @Override
                public void call(boolean result) {
                    profiles.remove(player.getUniqueId());
                }
            });
        }
    }

}