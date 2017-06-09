package me.joeleoli.practice.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public abstract class PluginCommand implements CommandExecutor, TabCompleter {

    protected Plugin plugin;

    public PluginCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            if (this.requiresPlayer() && !(sender instanceof Player)) {
                throw new CommandException(Collections.singletonList("You must be a player to execute this command."));
            }

            if (this.requiresPermission() && !sender.hasPermission(this.getPermission()) && !sender.isOp()) {
                throw new CommandException(Collections.singletonList("You do not have permission to execute this command."));
            }

            this.onCommand(sender, args);
        }
        catch (CommandException e) {
            for (String message : e.getMessages()) {
                sender.sendMessage(ChatColor.RED + message);
            }
        }

        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (this.requiresPlayer() && !(sender instanceof Player)) {
            return Collections.emptyList();
        }

        if (this.requiresPermission() && !sender.hasPermission(this.getPermission()) && !sender.isOp()) {
            return Collections.emptyList();
        }

        return this.onTabComplete(sender, args);
    }

    public abstract boolean requiresPlayer();

    public abstract boolean requiresPermission();

    public abstract String getPermission();

    protected abstract void onCommand(CommandSender sender, String[] args) throws CommandException;

    public abstract List<String> onTabComplete(CommandSender sender, String[] args);

}