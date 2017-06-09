package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class ChangeLogCommand extends PluginCommand {

    public ChangeLogCommand(Plugin plugin) {
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
        return false;
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GOLD + "PvPTemple Practice Changelog " + ChatColor.GREEN + "[5/22/2017]");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed player values not resetting after a party match.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed CHE that occurred when calculating permissions on login.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed players not being able to die in Party FFA.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed timers on scoreboards flickering between times.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed status counts to be more accurate and efficient.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed queue scoreboard going null when a player finds a match.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added spectating amount to tab and scoreboard.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added global ranking (based on global elo).");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added spectator scoreboard.");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added /ping [player].");
        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added /list.");
//        sender.sendMessage(ChatColor.GOLD + "PvPTemple Practice Changelog " + ChatColor.GREEN + "[5/18/2017]");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Your /settings will now be synced across every server.");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added sounds option to /settings.");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added /friend <player> (use to friend & un-friend).");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Added /ignore <player> (use to ignore & un-ignore).");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed dropping custom kit books to later re-kit.");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed picking up items after a match has finished.");
//        sender.sendMessage(ChatColor.GRAY + " * " + ChatColor.YELLOW + "Fixed negative queue & playing counts.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}