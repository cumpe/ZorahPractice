package me.joeleoli.practice.command.type;

import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class SaveDataCommand extends PluginCommand {

    public SaveDataCommand(Plugin plugin) {
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
    	sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE " + ChatColor.RESET + ChatColor.GREEN + "Starting task...");

        for (PracticeProfile profile : ManagerHandler.getPlayerManager().getAllData().values()) {
            profile.save(new GenericCallback() {
                @Override
                public void call(boolean result) {
                    if (result) {
                        sender.sendMessage(ChatColor.GREEN + "✓ Successfully saved " + profile.getPlayer().getName() + "'s data.");
                    }
                    else {
                        sender.sendMessage(ChatColor.RED + "✗ Failed to save " + profile.getPlayer().getName() + "'s data.");
                    }
                }
            });
        }

        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE " + ChatColor.RESET + ChatColor.GREEN + "Finished task...");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}