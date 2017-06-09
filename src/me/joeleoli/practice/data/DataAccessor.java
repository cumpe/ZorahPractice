package me.joeleoli.practice.data;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.UUID;

public class DataAccessor {

    public static PracticeProfile getPlayerProfile(Player player) {
        return ManagerHandler.getPlayerManager().getPlayerProfile(player);
    }

    public static PracticeProfile getPlayerProfile(UUID uuid) {
        return ManagerHandler.getPlayerManager().getPlayerProfile(uuid);
    }

    public static void setRating(UUID uuid, Ladder ladder, Integer rating, GenericCallback callback) {
        if (Bukkit.getPlayer(uuid) != null) {
            ManagerHandler.getPlayerManager().getPlayerProfile(uuid).setRating(ladder, 1000);
            ManagerHandler.getPlayerManager().getPlayerProfile(uuid).save(null);
            callback.call(true);
            return;
        }

        try {
            File file = new File(PracticePlugin.getInstance().getDataFolder() + "/playerdata/" + uuid.toString() + ".yml");

            if (!file.exists()) {
                callback.call(false);
                return;
            }

            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

            yml.set("ratings." + ladder.getName(), rating);
            yml.save(file);

            callback.call(true);
        }
        catch (Exception e) {
            callback.call(false);
            System.out.println("DataAccessor -> Failed method setRating");
            System.out.println(" * " + uuid.toString());
            System.out.println(" * " + ladder.getName());
            System.out.println(" * " + rating);
        }
    }

}