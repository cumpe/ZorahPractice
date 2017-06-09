package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerElo;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsCommand extends PluginCommand {

    public StatisticsCommand(Plugin plugin) {
        super(plugin);
    }

    public boolean requiresPermission() {
        return false;
    }

    public String getPermission() {
        return "";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to execute this command.");
            return;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            PracticeProfile playerData = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Statistics of " + ChatColor.DARK_PURPLE + player.getName());
            player.sendMessage(ChatColor.GOLD + "Global Rating" + ChatColor.GRAY + ": " + playerData.getGlobalRating());

            for (Map.Entry<Ladder, PlayerElo> entry : playerData.getLadderRatings().entrySet()) {
                player.sendMessage(ChatColor.YELLOW + entry.getKey().getName() + " Rating" + ChatColor.GRAY + ": " + entry.getValue().toInteger());
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Ranked Wins" + ChatColor.GRAY + ": " + playerData.getRankedWins());
            player.sendMessage(ChatColor.YELLOW + "Ranked Losses" + ChatColor.GRAY + ": " + playerData.getRankedLosses());
            player.sendMessage(ChatColor.YELLOW + "Unranked Wins" + ChatColor.GRAY + ": " + playerData.getUnrankedWins());
            player.sendMessage(ChatColor.YELLOW + "Unranked Losses" + ChatColor.GRAY + ": " + playerData.getUnrankedLosses());
            return;
        }

        if (Bukkit.getPlayer(args[0]) != null) {
            Player target = Bukkit.getPlayer(args[0]);
            PracticeProfile targetData = ManagerHandler.getPlayerManager().getPlayerProfile(target);

            player.sendMessage("");
            player.sendMessage(ChatColor.LIGHT_PURPLE + "Statistics of " + ChatColor.DARK_PURPLE + target.getName());
            player.sendMessage(ChatColor.GOLD + "Global Rating" + ChatColor.GRAY + ": " + targetData.getGlobalRating());

            for (Map.Entry<Ladder, PlayerElo> entry : targetData.getLadderRatings().entrySet()) {
                player.sendMessage(ChatColor.YELLOW + entry.getKey().getName() + " Rating" + ChatColor.GRAY + ": " + entry.getValue().toInteger());
            }

            player.sendMessage("");
            player.sendMessage(ChatColor.YELLOW + "Ranked Wins" + ChatColor.GRAY + ": " + targetData.getRankedWins());
            player.sendMessage(ChatColor.YELLOW + "Ranked Losses" + ChatColor.GRAY + ": " + targetData.getRankedLosses());
            player.sendMessage(ChatColor.YELLOW + "Unranked Wins" + ChatColor.GRAY + ": " + targetData.getUnrankedWins());
            player.sendMessage(ChatColor.YELLOW + "Unranked Losses" + ChatColor.GRAY + ": " + targetData.getUnrankedLosses());
        } else {
            player.sendMessage(ChatColor.RED + "That player is offline.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equals("")) {
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        } else {
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