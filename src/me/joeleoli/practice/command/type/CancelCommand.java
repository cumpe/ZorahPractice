package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CancelCommand extends PluginCommand {

    public CancelCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean requiresPermission() {
        return true;
    }
    
    @Override
    public String getPermission() {
        return "practice.admin";
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /cancel <player> <reason>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        PracticeProfile targetData = ManagerHandler.getPlayerManager().getPlayerProfile(target);

        if (targetData.getStatus() != PlayerStatus.PLAYING) {
            sender.sendMessage(ChatColor.RED + "That player is not in a match.");
            return;
        }

        String message = "";

        if (args.length > 1) {
            for (int i = 1; i > args.length; i++) {
                message += args[i];
            }
        } else {
            message = "Unspecified";
        }

        targetData.getCurrentMatch().cancelMatch("Staff (" + message + ")");
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