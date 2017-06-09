package me.joeleoli.practice.game.duel.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelHelpArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return true;
    }

    public boolean requiresPermission() {
        return false;
    }

    public String getPermission() {
        return "";
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " Duel Help");
        sender.sendMessage(ChatColor.YELLOW + "/duel <player> " + ChatColor.GRAY + " - Duel a player");
        sender.sendMessage(ChatColor.YELLOW + "/duel accept <player> " + ChatColor.GRAY + " - Accept a duel request.");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}