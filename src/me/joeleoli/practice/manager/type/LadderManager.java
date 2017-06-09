package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.file.FileConfig;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.util.InventoryUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class LadderManager {

    @Getter private FileConfig gameConfig;
    @Getter private Map<String, Ladder> ladders;

    public LadderManager() {
        gameConfig = new FileConfig("ladders.yml");
        ladders = new HashMap<>();

        loadLadders();
    }

    public Ladder getLadderByName(String name) {
        return ladders.get(name);
    }

    public int getLadderAmount() {
        if (ladders.size() < 9) {
            return 9;
        } else if (ladders.size() > 9 && ladders.size() < 19) {
            return 18;
        } else if (ladders.size() > 18 && ladders.size() < 28) {
            return 27;
        } else if (ladders.size() > 27 && ladders.size() < 37) {
            return 36;
        } else {
            return 45;
        }
    }

    private void loadLadders() {
        FileConfiguration config = gameConfig.getConfig();

        if (config.getConfigurationSection("ladder") == null) {
            PracticePlugin.getInstance().getLogger().info("There are no ladders stored in the configuration.");
            return;
        }

        for (String s : config.getConfigurationSection("ladder").getKeys(false)) {
            try {
                Ladder ladder = new Ladder(s);

                if (!config.contains("ladder." + s + ".displayName")) {
                    ladder.setDisplayName(s);
                } else {
                    ladder.setDisplayName(config.getString("ladder." + s + ".displayName"));
                }

                if (config.contains("ladder." + s + ".displayIcon")) {
                    ladder.setDisplayIcon(InventoryUtils.itemStackFromString(config.getString("ladder." + s + ".displayIcon")));
                } else {
                    ladder.setDisplayIcon(new ItemStack(Material.DIAMOND_SWORD));
                }

                if (config.contains("ladder." + s + ".displayOrder")) {
                    ladder.setDisplayOrder(config.getInt("ladder." + s + ".displayOrder"));
                } else {
                    ladder.setDisplayOrder(0);
                }

                ladder.setDefaultInventory(InventoryUtils.playerInventoryFromString(config.getString("ladder." + s + ".defaultInventory")));

                if (config.contains("ladder." + s + ".hitDelay")) {
                    ladder.setHitDelay(config.getInt("ladder." + s + ".hitDelay"));
                }

                if (config.getString("ladder." + s + ".allowEdit") != null) {
                    ladder.allowEdit(config.getBoolean("ladder." + s + ".allowEdit"));
                }

                if (config.getString("ladder." + s + ".allowHeal") != null) {
                    ladder.allowHeal(config.getBoolean("ladder." + s + ".allowHeal"));
                }

                if (config.getString("ladder." + s + ".allowHunger") != null) {
                    ladder.allowHunger(config.getBoolean("ladder." + s + ".allowHunger"));
                }

                ladders.put(s, ladder);

                ManagerHandler.getStorageBackend().addColumn(ladder.getName());
            }
            catch (Exception e) {
                PracticePlugin.getInstance().getLogger().severe("[ARENAS] Failed to load arena '" + s + "'!");
            }
        }
    }

}