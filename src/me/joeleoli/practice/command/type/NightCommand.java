package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class NightCommand extends PluginCommand {

    public NightCommand(Plugin plugin) {
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
        if (!(sender instanceof Player)) throw new CommandException(Collections.singletonList("You must be a player to execute this command."));

        Player player = (Player)sender;
        player.setPlayerTime(18000, false);
        player.sendMessage(ChatColor.GREEN + "Set time to night.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}