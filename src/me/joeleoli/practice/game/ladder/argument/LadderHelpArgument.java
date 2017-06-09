package me.joeleoli.practice.game.ladder.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class LadderHelpArgument extends PluginCommandArgument {

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
        sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + " Ladder Help");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder create » " + ChatColor.WHITE + "Creates a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder delete » " + ChatColor.WHITE + "Deletes a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setdisplayname <string> » " + ChatColor.WHITE + "Sets the display name of a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setorder <int> » " + ChatColor.WHITE + "Sets the order of a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setinv » " + ChatColor.WHITE + "Sets the default inventory of a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder loadinv » " + ChatColor.WHITE + "Loads the default inventory of a ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder sethitdelay <int> » " + ChatColor.WHITE + "Sets the hit delay of the ladder.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setallowedit <bool> » " + ChatColor.WHITE + "Sets the ladder's allow-edit variable.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setallowheal <bool> » " + ChatColor.WHITE + "Sets the ladder's allow-heal variable.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder setallowhunger <bool> » " + ChatColor.WHITE + "Sets the ladder's allow-hunger variable.");
        sender.sendMessage(ChatColor.YELLOW + "   /ladder list » " + ChatColor.WHITE + "Lists all ladders.");
        sender.sendMessage(ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}