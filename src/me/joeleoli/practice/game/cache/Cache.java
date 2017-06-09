package me.joeleoli.practice.game.cache;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.InventoryUtils;
import me.joeleoli.practice.util.TimeUtil;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Cache {

    public static int playingAmount, queueingAmount, spectatingAmount = 0;
    public static Map<UUID, CachedInventory> inventories = new HashMap<>();
    public static Map<UUID, Integer> rankings = new HashMap<>();

    public Cache() {
        new BukkitRunnable() {
            @Override
            public void run() {
                int play = 0;
                int queue = 0;
                int spectate = 0;

                if (!ManagerHandler.getPlayerManager().getProfiles().values().isEmpty()) {
                    for (PracticeProfile profile : ManagerHandler.getPlayerManager().getProfiles().values()) {
                        if (profile.getStatus() == PlayerStatus.PLAYING) {
                            play++;
                        }
                        else if (profile.getStatus() == PlayerStatus.QUEUEING) {
                            queue++;
                        }
                        else if (profile.getStatus() == PlayerStatus.SPECTATING) {
                            spectate++;
                        }
                    }
                }

                playingAmount = play;
                queueingAmount = queue;
                spectatingAmount = spectate;
            }
        }.runTaskTimerAsynchronously(PracticePlugin.getInstance(), 0L, 10L);

        new BukkitRunnable() {
            public void run() {
                rankings.clear();
                ManagerHandler.getStorageBackend().updateGlobalRanks();
            }
        }.runTaskTimerAsynchronously(PracticePlugin.getInstance(), 0L, 20L * 60);
    }

    public static void storeInventory(Player player, boolean dead) {
        List<String> effects = new ArrayList<>();
        player.getActivePotionEffects().forEach(effect -> effects.add(effect.getType().getName() + " " + effect.getAmplifier() + " (" + TimeUtil.formatSeconds(effect.getDuration() / 20) + ")"));
        CachedInventory inv = getCachedInventory(player, dead);

        if (inventories.containsKey(player.getUniqueId())) {
            inventories.replace(player.getUniqueId(), inv);
        }
        else {
            inventories.put(player.getUniqueId(), inv);
        }

        new BukkitRunnable() {
            public void run() {
                if (inventories.containsKey(player.getUniqueId()) && inventories.get(player.getUniqueId()).getIdentifier().equals(inv.getIdentifier())) {
                    inventories.remove(player.getUniqueId());
                }
            }
        }.runTaskLater(PracticePlugin.getInstance(), 20L * 60);
    }

    public static void storeInventory(UUID uuid, CachedInventory inventory) {
        if (inventories.containsKey(uuid)) {
            inventories.replace(uuid, inventory);
        }
        else {
            inventories.put(uuid, inventory);
        }
    }

    public static CachedInventory getCachedInventory(Player player, boolean dead) {
        List<String> effects = new ArrayList<>();
        player.getActivePotionEffects().forEach(effect -> effects.add(effect.getType().getName() + " " + effect.getAmplifier() + " (" + TimeUtil.formatSeconds(effect.getDuration() / 20) + ")"));
        return new CachedInventory(player.getName(), (dead ? 0.0 : Math.round(player.getHealth())), player.getFoodLevel(), effects, InventoryUtils.playerInventoryFromPlayer(player));
    }

}