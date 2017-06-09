package me.joeleoli.practice.menu;

import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.util.ItemBuilder;
import me.joeleoli.practice.util.MessageUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DuelMenu {

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Send Duel Request");

        if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
            for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                inv.setItem(ladder.getDisplayOrder(), new ItemBuilder(ladder.getDisplayIcon().getType(), MessageUtils.color(ladder.getDisplayName()), 1, ladder.getDisplayIcon().getDurability(), ChatColor.GRAY + "Click to edit this ladder's kits.").getItem());
            }
        }

        player.openInventory(inv);
    }

}