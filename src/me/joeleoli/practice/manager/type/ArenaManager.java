package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.util.LocationUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ArenaManager {

    @Getter private Map<String, Arena> arenas;
    
    public ArenaManager() {
        this.arenas = new HashMap<>();
        initiateArenas();
    }
    
    public Arena getArena(String name) {
        return this.arenas.get(name);
    }
    
    public boolean isArena(String name) {
        return this.arenas.containsKey(name);
    }
    
    public Arena getRandomArena() {
        if (this.arenas.isEmpty()) return null;

        Random random = new Random();
        return (Arena)arenas.values().toArray()[random.nextInt(arenas.values().size())];
    }
    
    public void addArena(String name, Arena arena) {
        this.arenas.put(name, arena);
    }
    
    public void removeArena(String name) {
        this.arenas.remove(name);
    }
    
    private void initiateArenas() {
        FileConfiguration config = ManagerHandler.getConfig().getArenasConfig().getConfig();

        if (!config.contains("arenas")) return;

        for (String name : config.getConfigurationSection("arenas").getKeys(false)) {
            try {
                String displayName = config.getString("arenas." + name + ".display-name");
                Integer displayOrder = config.getInt("arenas." + name + ".display-order");
                Location location1 = LocationUtils.getLocation(config.getString("arenas." + name + ".location1"));
                Location location2 = LocationUtils.getLocation(config.getString("arenas." + name + ".location2"));
                Arena arena = new Arena(name, displayName, displayOrder, location1, location2);
                this.arenas.put(name, arena);
            }
            catch (Exception e) {
                PracticePlugin.getInstance().getLogger().severe("Failed to load arena '" + name + "', stack trace below:");
                PracticePlugin.getInstance().getLogger().severe("------------------------------------------------------");
                e.printStackTrace();
                PracticePlugin.getInstance().getLogger().severe("------------------------------------------------------");
            }
        }
    }

}