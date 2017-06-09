package com.alexandeh.kraken;

import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.event.PlayerTabRemoveEvent;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;

@Getter
public class Kraken implements Listener {

    private static Kraken instance;

    public Kraken() {
        if (Bukkit.getMaxPlayers() < 60) {
            throw new NumberFormatException("Player limit must be at least 60!");
        }

        instance = this;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkPlayer(player);
                }
            }
        }.runTaskLaterAsynchronously(PracticePlugin.getInstance(), 4L);

        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                checkPlayer(player);
            }
        }.runTaskLaterAsynchronously(PracticePlugin.getInstance(), 4L);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerTab playerTab = PlayerTab.getByPlayer(player);

        if (playerTab != null) {
            for (Team team : new HashSet<>(playerTab.getScoreboard().getTeams())) {
                team.unregister();
            }

            PlayerTab.getPlayerTabs().remove(playerTab);
            Bukkit.getPluginManager().callEvent(new PlayerTabRemoveEvent(playerTab));
        }
    }

    private void checkPlayer(Player player) {
        PlayerTab playerTab = PlayerTab.getByPlayer(player);

        if (playerTab == null) {
            new PlayerTab(player);
        } else {
            playerTab.clear();
        }
    }

    public static Kraken getInstance() {
        return instance;
    }
}
