package me.joeleoli.practice;

import com.alexandeh.kraken.Kraken;
import lombok.Getter;

import me.joeleoli.practice.data.file.FileConfig;
import me.joeleoli.practice.util.LocationUtils;

import me.joeleoli.practice.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PracticeConfiguration {

    public static String SCOREBOARD_TITLE = ChatColor.AQUA + ChatColor.BOLD.toString() + "PvPTemple";
    public static String SCOREBOARD_SPACER = MessageUtils.color("&7&m-------");
    public static String SCOREBOARD_SPACER_LARGE = MessageUtils.color("&7&m----------");

    public static Boolean USE_TAB = true;

    @Getter private FileConfig rootConfig;
    @Getter private FileConfig arenasConfig;
    @Getter private FileConfig laddersConfig;

    @Getter private Location spawnPoint;
    @Getter private Location editorPoint;

    public PracticeConfiguration() {
        this.rootConfig = new FileConfig("config.yml");
        this.arenasConfig = new FileConfig("arenas.yml");
        this.laddersConfig = new FileConfig("ladders.yml");

        try {
            this.spawnPoint = LocationUtils.getLocation(this.rootConfig.getConfig().getString("setup.spawn-location"));
            this.editorPoint = LocationUtils.getLocation(this.rootConfig.getConfig().getString("setup.editor-location"));

            USE_TAB = this.rootConfig.getConfig().getBoolean("tab.enabled");

            if (USE_TAB) {
                new Kraken();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setSpawnPoint(Location location) {
        this.spawnPoint = location;
        this.rootConfig.getConfig().set("setup.spawn-location", LocationUtils.getString(location));
        this.rootConfig.save();
    }
    
    public void setEditorPoint(Location location) {
        this.editorPoint = location;
        this.rootConfig.getConfig().set("setup.editor-location", LocationUtils.getString(location));
        this.rootConfig.save();
    }
    
    public void teleportToSpawn(Player player) {
        if (this.spawnPoint != null) {
            player.teleport(this.spawnPoint);
        }
    }
    
    public void teleportToEditor(Player player) {
        if (this.editorPoint != null) {
            player.teleport(this.editorPoint);
        }
    }

}