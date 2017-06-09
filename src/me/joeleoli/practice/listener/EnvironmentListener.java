package me.joeleoli.practice.listener;

import me.joeleoli.practice.PracticePlugin;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class EnvironmentListener implements Listener {

    public EnvironmentListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());

        new BukkitRunnable() {
            public void run() {
                for(World world : Bukkit.getWorlds()) {
                    world.setTime(6000L);
                }
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L * 3);
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onWeatherChange(WeatherChangeEvent event) {
        boolean rain = event.toWeatherState();

        if (rain) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onThunderChange(ThunderChangeEvent event) {
        boolean storm = event.toThunderState();

        if (storm) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.getEntity().remove();
        }
    }

}