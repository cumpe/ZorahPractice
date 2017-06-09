package me.joeleoli.practice.game.arena;

import lombok.Getter;
import lombok.Setter;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.util.LocationUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class Arena {

    @Getter private String name;
    @Getter @Setter private String displayName;
    @Getter @Setter private Integer displayOrder;
    @Getter @Setter private Location location1;
    @Getter @Setter private Location location2;

    public Arena(String name) {
        this(name, "&b" + name, 0, null, null);
    }
    
    public Arena(String name, String displayName, Integer displayOrder, Location loc1, Location loc2) {
        this.name = name;
        this.displayName = displayName;
        this.displayOrder = displayOrder;
        this.location1 = loc1;
        this.location2 = loc2;
    }
    
    public void save() {
        FileConfiguration config = ManagerHandler.getConfig().getArenasConfig().getConfig();

        config.set("arenas." + this.name + ".display-name", this.displayName);
        config.set("arenas." + this.name + ".display-order", this.displayOrder);

        if (this.location1 != null) {
            config.set("arenas." + this.name + ".location1", LocationUtils.getString(this.location1));
        } else {
            config.set("arenas." + this.name + ".location1", "unset");
        }

        if (this.location2 != null) {
            config.set("arenas." + this.name + ".location2", LocationUtils.getString(this.location2));
        } else {
            config.set("arenas." + this.name + ".location2", "unset");
        }

        ManagerHandler.getConfig().getArenasConfig().save();
    }
    
    public void save(CommandSender sender) {
        this.save();
        sender.sendMessage(ChatColor.GREEN + "Successfully saved arena (" + this.name + ")");
    }
    
    public void remove() {
        FileConfiguration config = ManagerHandler.getConfig().getArenasConfig().getConfig();
        config.set("arenas." + this.name, null);
        ManagerHandler.getConfig().getArenasConfig().save();
    }

}