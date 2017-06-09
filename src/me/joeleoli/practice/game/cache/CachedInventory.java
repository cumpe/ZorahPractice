package me.joeleoli.practice.game.cache;

import lombok.Getter;
import me.joeleoli.practice.player.PlayerInv;
import me.joeleoli.practice.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class CachedInventory {

	@Getter private UUID identifier;
    @Getter private String name;
    @Getter private double health;
    @Getter private double food;
    private List<String> effects;
    private PlayerInv inventory;
    
    public CachedInventory(String name, double health, double food, List<String> effects, PlayerInv inventory) {
    	this.identifier = UUID.randomUUID();
        this.name = name;
        this.health = health;
        this.food = food;
        this.effects = effects;
        this.inventory = inventory;
    }
    
    public Inventory getInventory() {
        Inventory inv = Bukkit.createInventory(null, 54, "Inventory of " + this.name);

        for (int i = 9; i <= 35; ++i) {
            inv.setItem(i - 9, this.inventory.getContents()[i]);
        }

        for (int i = 0; i <= 8; ++i) {
            inv.setItem(i + 27, this.inventory.getContents()[i]);
        }

        inv.setItem(36, this.inventory.getHelmet());
        inv.setItem(37, this.inventory.getChestPiece());
        inv.setItem(38, this.inventory.getLeggings());
        inv.setItem(39, this.inventory.getBoots());

        if (this.health == 0.0) {
            inv.setItem(48, new ItemBuilder(Material.SKULL_ITEM, ChatColor.RED + "Player Died", new String[] { "" }).getItem());
        }
        else {
            inv.setItem(48, new ItemBuilder(Material.SPECKLED_MELON, ChatColor.GREEN + "Player Health", new String[] { this.health / 2.0 + " Hearts" }).getItem());
        }

        inv.setItem(49, new ItemBuilder(Material.COOKED_BEEF, ChatColor.GREEN + "Player Hunger", new String[] { this.food / 2.0 + " Hunger" }).getItem());
        ItemStack potions = new ItemBuilder(Material.POTION, ChatColor.BLUE + "Potion Effects", new String[0]).getItem();
        ItemMeta imm = potions.getItemMeta();
        imm.setLore(this.effects);
        potions.setItemMeta(imm);
        inv.setItem(50, potions);

        return inv;
    }

}
