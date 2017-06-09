package me.joeleoli.practice.menu;

import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PartyEventMenu {

    public static void open(Player player) {
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getParty() == null) {
            player.sendMessage(ChatColor.RED + "You must be in a party to open the event menu.");
            return;
        }

        if (profile.getParty().getPlayers().size() < 2) {
            player.sendMessage(ChatColor.RED + "You must have more than 2 players in your party to start an event.");
            player.closeInventory();
            return;
        }

        Inventory inv = Bukkit.createInventory(null, ManagerHandler.getLadderManager().getLadderAmount(), ChatColor.AQUA + "Party Events - Select an event");
        inv.setItem(2, new ItemBuilder(Material.DIAMOND, ChatColor.AQUA + "Team Deathmatch", ChatColor.GRAY + "Click to select the", ChatColor.GRAY + "Team Deathmatch event.").getItem());
        inv.setItem(6, new ItemBuilder(Material.DIAMOND, ChatColor.AQUA + "Free-for-All", ChatColor.GRAY + "Click to select the", ChatColor.GRAY + "Free-for-All event.").getItem());

        player.openInventory(inv);
    }

}