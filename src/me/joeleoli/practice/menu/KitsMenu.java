package me.joeleoli.practice.menu;

import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.ItemBuilder;
import me.joeleoli.practice.util.MessageUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class KitsMenu {

    public static void open(Player player) {
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.LOBBY) {
            player.sendMessage(ChatColor.RED + "You must be in the lobby to open that menu.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, ManagerHandler.getLadderManager().getLadderAmount(), ChatColor.RED + "Kit Editor");

        if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
            for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                if (ladder.allowEdit()) {
                    inv.setItem(ladder.getDisplayOrder(), new ItemBuilder(ladder.getDisplayIcon().getType(), MessageUtils.color(ladder.getDisplayName()), 1, ladder.getDisplayIcon().getDurability(), ChatColor.GRAY + "Click to edit this ladder's kits.").getItem());
                }
            }
        }

        player.openInventory(inv);
    }

}