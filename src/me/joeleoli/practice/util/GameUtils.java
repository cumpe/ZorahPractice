package me.joeleoli.practice.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameUtils {

    public static void resetPlayer(Player player) {
        player.setCanPickupItems(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFireTicks(1);
        player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
    }
    
    public static ItemStack[] getLobbyInventory() {
        return new ItemStack[] {
                PlayerItem.UNRANKED_QUEUE.getItem(),
                PlayerItem.RANKED_QUEUE.getItem(),
                PlayerItem.FFA.getItem(),
                null,
                PlayerItem.CREATE_PARTY.getItem(),
                PlayerItem.SPECTATOR_MODE.getItem(),
                null,
                PlayerItem.KIT_EDTIOR.getItem(),
                PlayerItem.SETTINGS.getItem()
        };
    }
    
    public static ItemStack[] getPartyLeaderInventory() {
        return new ItemStack[] {
                PlayerItem.PARTY_QUEUE.getItem(),
                null,
                PlayerItem.PARTY_INFORMATION.getItem(),
                PlayerItem.PARTY_EVENTS.getItem(),
                PlayerItem.VIEW_PARTIES.getItem(),
                null,
                PlayerItem.DISBAND_PARTY.getItem(),
                null,
                PlayerItem.SETTINGS.getItem()
        };
    }
    
    public static ItemStack[] getPartyMemberInventory() {
        return new ItemStack[] {
                PlayerItem.PARTY_INFORMATION.getItem(),
                PlayerItem.LEAVE_PARTY.getItem(),
                null,
                PlayerItem.VIEW_PARTIES.getItem(),
                null,
                null,
                null,
                PlayerItem.KIT_EDTIOR.getItem(),
                PlayerItem.SETTINGS.getItem()
        };
    }
    
    public static ItemStack[] getQueueInventory() {
        return new ItemStack[] {
                PlayerItem.LEAVE_QUEUE.getItem(),
                PlayerItem.QUEUE_INFO.getItem(),
                null,
                null,
                null,
                null,
                null,
                null,
                PlayerItem.SETTINGS.getItem()
        };
    }

    public static ItemStack[] getSpectatorInventory() {
        return new ItemStack[] {
                PlayerItem.STOP_SPECTATING.getItem(),
                PlayerItem.SPECTATOR_INFO.getItem(),
                null,
                null,
                null,
                null,
                null,
                PlayerItem.SPECTATOR_MODE.getItem(),
                PlayerItem.SETTINGS.getItem()
        };
    }
    
    public enum PlayerItem {
        SETTINGS(new ItemBuilder(Material.ANVIL, ChatColor.LIGHT_PURPLE + "Settings", ChatColor.GRAY + "Right-click to open", ChatColor.GRAY + "your settings.")),
        SPECTATOR_MODE(new ItemBuilder(Material.REDSTONE_TORCH_ON, ChatColor.RED + "Spectator Mode", ChatColor.GRAY + "Right-click to toggle", ChatColor.GRAY + "spectator mode.")),
        PARTY_QUEUE(new ItemBuilder(Material.IRON_SWORD, ChatColor.YELLOW + "2v2 Queue", ChatColor.GRAY + "Right-click to join a queue.")),
        RANKED_QUEUE(new ItemBuilder(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked Matches", ChatColor.GRAY + "Right-click to join a queue.")),
        UNRANKED_QUEUE(new ItemBuilder(Material.IRON_SWORD, ChatColor.YELLOW + "Unranked Matches", ChatColor.GRAY + "Right-click to join a queue.")),
        FFA(new ItemBuilder(Material.GOLD_AXE, ChatColor.YELLOW + "FFA", ChatColor.GRAY + "Right-click to join FFA.")),
        KIT_EDTIOR(new ItemBuilder(Material.BOOK, ChatColor.YELLOW + "Edit Kits", ChatColor.GRAY + "Right-click to edit your kits.")),
        PARTY_EVENTS(new ItemBuilder(Material.EYE_OF_ENDER, ChatColor.AQUA + "Party Event", ChatColor.GRAY + "Right-click to start a party event.")),
        CREATE_PARTY(new ItemBuilder(Material.NAME_TAG, ChatColor.AQUA + "Create Party",  ChatColor.GRAY + "Right-click to create a party.")),
        DISBAND_PARTY(new ItemBuilder(Material.FIREBALL, ChatColor.RED + "Disband Party", ChatColor.GRAY + "Right-click to disband your party.")),
        LEAVE_PARTY(new ItemBuilder(Material.FIREBALL, ChatColor.RED + "Leave Party", ChatColor.GRAY + "Right-click to leave your party.")),
        VIEW_PARTIES(new ItemBuilder(Material.ENDER_CHEST, ChatColor.GREEN + "View Parties", ChatColor.GRAY + "Right-click to view other parties.")),
        PARTY_INFORMATION(new ItemBuilder(Material.CHEST, ChatColor.GREEN + "Party Members", 1, (short)3, ChatColor.GRAY + "Right-click to view party ", ChatColor.GRAY + "members.")),
        LEAVE_QUEUE(new ItemBuilder(Material.INK_SACK, ChatColor.RED + "Leave Queue", 1, (short)1, ChatColor.GRAY + "Right-click to leave your queue.")),
        STOP_SPECTATING(new ItemBuilder(Material.INK_SACK, ChatColor.RED + "Stop Spectating", 1, (short)1, ChatColor.GRAY + "Right-click to stop spectating.")),
        QUEUE_INFO(new ItemBuilder(Material.PAPER, ChatColor.YELLOW + "Queue Information", ChatColor.GRAY + "Right-click to get queue", ChatColor.GRAY + "information.")),
        SPECTATOR_INFO(new ItemBuilder(Material.PAPER, ChatColor.YELLOW + "Spectator Information", ChatColor.GRAY + "Right-click to get", ChatColor.GRAY + "spectator information."));
        
        private ItemBuilder builder;
        
        PlayerItem(ItemBuilder builder) {
            this.builder = builder;
        }
        
        public ItemStack getItem() {
            return this.builder.getItem();
        }
    }

}