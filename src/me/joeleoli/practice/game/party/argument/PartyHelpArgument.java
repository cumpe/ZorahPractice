package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyHelpArgument extends PluginCommandArgument {

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
        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
        sender.sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + " Party Help");
        sender.sendMessage(ChatColor.AQUA + "   /party create » " + ChatColor.GRAY + "Creates a party.");
        sender.sendMessage(ChatColor.AQUA + "   /party disband » " + ChatColor.GRAY + "Disbands a party.");
        sender.sendMessage(ChatColor.AQUA + "   /party invite » " + ChatColor.GRAY + "Invites a player.");
        sender.sendMessage(ChatColor.AQUA + "   /party kick » " + ChatColor.GRAY + "Removes a player.");
        sender.sendMessage(ChatColor.AQUA + "   /party accept » " + ChatColor.GRAY + "Accepts an invite.");
        sender.sendMessage(ChatColor.AQUA + "   /party decline » " + ChatColor.GRAY + "Declines an invite.");
        sender.sendMessage(ChatColor.AQUA + "   /party info » " + ChatColor.GRAY + "Shows info about party.");
        sender.sendMessage(ChatColor.DARK_GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}