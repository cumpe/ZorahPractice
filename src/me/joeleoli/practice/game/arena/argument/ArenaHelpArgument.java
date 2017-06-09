package me.joeleoli.practice.game.arena.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaHelpArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

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

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " Arena Help");
        sender.sendMessage(ChatColor.YELLOW + "   /arena create » " + ChatColor.WHITE + "Creates an arena.");
        sender.sendMessage(ChatColor.YELLOW + "   /arena delete » " + ChatColor.WHITE + "Deletes an arena.");
        sender.sendMessage(ChatColor.YELLOW + "   /arena setspawn1 » " + ChatColor.WHITE + "Sets the 1st spawn point of an arena.");
        sender.sendMessage(ChatColor.YELLOW + "   /arena setspawn2 » " + ChatColor.WHITE + "Sets the 2nd spawn point of an arena.");
        sender.sendMessage(ChatColor.YELLOW + "   /arena wand » " + ChatColor.WHITE + "Gives the selection wand.");
        sender.sendMessage(ChatColor.YELLOW + "   /arena list » " + ChatColor.WHITE + "Lists all arenas.");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}