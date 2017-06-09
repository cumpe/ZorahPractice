package me.joeleoli.practice.game.ladder;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerInv;
import me.joeleoli.practice.util.InventoryUtils;
import me.joeleoli.practice.util.ItemBuilder;
import me.joeleoli.practice.util.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Ladder {

    @Getter private String name;
    @Getter @Setter private String displayName;
    @Getter @Setter private ItemStack displayIcon;
    @Getter @Setter private Integer displayOrder;
    @Getter @Setter private PlayerInv defaultInventory;
    @Getter @Setter private Integer hitDelay;
    @Getter @Setter @Accessors(fluent = true) private Boolean allowEdit;
    @Getter @Setter @Accessors(fluent = true) private Boolean allowHeal;
    @Getter @Setter @Accessors(fluent = true) private Boolean allowHunger;

    public Ladder(String name) {
        this.name = name;
        this.displayName = "&b" + name;
        this.displayIcon = new ItemBuilder(Material.DIAMOND_SWORD, MessageUtils.color(displayName)).getItem();
        this.displayOrder = 0;
        this.hitDelay = 20;
        this.allowEdit = true;
        this.allowHeal = true;
        this.allowHunger = true;
    }

    public void save(CommandSender sender) {
        save();
        sender.sendMessage(ChatColor.GRAY + "You have saved the ladder " + ChatColor.AQUA + name + ChatColor.GRAY + ".");
    }

    public void save() {
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".displayName", displayName);
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".displayIcon", InventoryUtils.itemStackToString(displayIcon));
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".displayOrder", displayOrder);
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".defaultInventory", InventoryUtils.playerInvToString(defaultInventory));
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".hitDelay", hitDelay);
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".allowEdit", allowEdit);
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".allowHeal", allowHeal);
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name + ".allowHunger", allowHunger);
        ManagerHandler.getConfig().getLaddersConfig().save();
    }

    public void delete(CommandSender sender) {
        delete();
        sender.sendMessage(ChatColor.GRAY + "You have deleted the ladder " + ChatColor.AQUA + name + ChatColor.GRAY + ".");
    }

    private void delete() {
        ManagerHandler.getConfig().getLaddersConfig().getConfig().set("ladder." + name, null);
        ManagerHandler.getConfig().getLaddersConfig().save();
    }

}