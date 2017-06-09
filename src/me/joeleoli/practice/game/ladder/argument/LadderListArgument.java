package me.joeleoli.practice.game.ladder.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LadderListArgument extends PluginCommandArgument {

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
        StringBuilder builder = new StringBuilder(ChatColor.GOLD + "Ladder(s): " + ChatColor.GRAY);

        ManagerHandler.getLadderManager().getLadders().values().forEach(ladder -> {
            builder.append(ladder.getName() + ChatColor.GRAY + ", ");
        });

        sender.sendMessage(builder.toString());
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}