package me.joeleoli.practice.command.type.variable.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class VariableHelpArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.singletonList("h");

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return false;
    }

    public boolean requiresPermission() {
        return true;
    }

    public String getPermission() {
        return "practice.admin";
    }

    public VariableHelpArgument() {
        this.aliases = Collections.singletonList("rr");
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");
        sender.sendMessage(ChatColor.GOLD + " Variable Help");
        sender.sendMessage(ChatColor.GRAY + "    * " + ChatColor.GOLD + "/var sr <player> <ladder> <elo>" + ChatColor.GRAY + " - Set a player's ladder rating.");
        sender.sendMessage(ChatColor.GRAY + "    * " + ChatColor.GOLD + "/var rr <player> <ladder>" + ChatColor.GRAY + " - Reset a player's ladder rating.");
        sender.sendMessage(ChatColor.GRAY + "    * " + ChatColor.GOLD + "/var ra <player>" + ChatColor.GRAY + " - Reset all ratings of a player.");
        sender.sendMessage(ChatColor.GOLD + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}