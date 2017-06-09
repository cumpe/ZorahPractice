package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryCommand extends PluginCommand {

    public InventoryCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean requiresPermission() {
        return false;
    }
    
    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player) sender;
        PracticeProfile profile = DataAccessor.getPlayerProfile(player);

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /inv <player>");
            return;
        }

        @SuppressWarnings("deprecation")
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That inventory could not be found.");
            return;
        }

        if (profile.getStatus() != PlayerStatus.LOBBY) {
            throw new CommandException(Collections.singletonList("You cannot open a cached inventory while in a match."));
        }

        if (Cache.inventories.containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.GRAY + "Viewing the inventory of " + ChatColor.AQUA + args[0] + ChatColor.GRAY + "...");
            player.openInventory(Cache.inventories.get(target.getUniqueId()).getInventory());
        }
        else {
            player.sendMessage(ChatColor.RED + "That player's inventory has not been stored recently.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equals("")) {
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        }
        else {
            ArrayList<String> returnList = new ArrayList<>();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    returnList.add(p.getName());
                }
            }

            return returnList;
        }
    }

}