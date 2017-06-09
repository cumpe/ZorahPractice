package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.party.PartyStatus;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyListArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return false;
    }

    public boolean requiresPermission() {
        return false;
    }

    public String getPermission() {
        return "";
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player) sender;
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
        Party party = profile.getParty();

        if (party == null) {
            throw new CommandException(Collections.singletonList("You do not have a party."));
        }

        boolean isLeader = false;

        if (profile.getParty().getLeader().equals(player)) {
            isLeader = true;
        }

        Inventory inv = Bukkit.createInventory(null, ManagerHandler.getPartyManager().getPartyInvAmount(), ChatColor.GOLD + "Party List");

        if (!ManagerHandler.getPartyManager().getParties().isEmpty()) {
            int i = 0;

            for (Party pl : ManagerHandler.getPartyManager().getParties().values()) {
                if (pl.equals(profile.getParty())) continue;
                if (pl.getStatus() != PartyStatus.IDLE) continue;

                ItemStack item = new ItemStack(Material.ENDER_CHEST);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD + "Party of " + pl.getLeader().getName());

                List<String> lore = new ArrayList<>();

                int j = 0;

                for (Player p : pl.getPlayers()) {
                    if (j > 6) {
                        lore.add("and more...");
                        break;
                    }

                    lore.add(p.getName());
                    j++;
                }

                if (isLeader) {
                    lore.add(ChatColor.GRAY + "Click to send this party a duel.");
                } else {
                    lore.add(ChatColor.GRAY + "Click to view this party's info.");
                }

                meta.setLore(lore);
                item.setItemMeta(meta);

                inv.setItem(i, item);

                i++;
            }
        }

        player.openInventory(inv);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}