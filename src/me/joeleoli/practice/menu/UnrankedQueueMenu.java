package me.joeleoli.practice.menu;

import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.type.SoloQueue;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.HiddenStringUtil;
import me.joeleoli.practice.util.ItemBuilder;
import me.joeleoli.practice.util.MessageUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class UnrankedQueueMenu {

    public static void open(Player player) {
        PracticeProfile playerData = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (playerData.getStatus() != PlayerStatus.LOBBY) {
            player.sendMessage(ChatColor.RED + "You must be in the lobby to open that menu.");
            return;
        }

        Inventory inv = Bukkit.createInventory(null, ManagerHandler.getLadderManager().getLadderAmount(), ChatColor.GREEN + "Unranked Queue");

        if (!ManagerHandler.getQueueManager().getQueues().isEmpty()) {
            for (IQueue queue : ManagerHandler.getQueueManager().getQueues().values()) {
                if (queue instanceof SoloQueue && !queue.isRanked()) {
                    inv.setItem(queue.getLadder().getDisplayOrder(), new ItemBuilder(queue.getLadder().getDisplayIcon().getType(), MessageUtils.color(queue.getLadder().getDisplayName()), 1, queue.getLadder().getDisplayIcon().getDurability(), ChatColor.YELLOW + "In Fights: " + ChatColor.GRAY + queue.getPlayingAmount(), ChatColor.YELLOW + "In Queue: " + ChatColor.GRAY + queue.getQueueingAmount(), "", ChatColor.GRAY + "Click to join this queue.", HiddenStringUtil.encodeString(queue.getIdentifier().toString())).getItem());
                }
            }
        }

        player.openInventory(inv);
    }

}