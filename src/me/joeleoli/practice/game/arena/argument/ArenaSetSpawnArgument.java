package me.joeleoli.practice.game.arena.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaSetSpawnArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return true;
    }

    public boolean requiresPermission() {
        return true;
    }

    public String getPermission() {
        return "practice.admin";
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new CommandException(Collections.singletonList("Usage: /arena setspawn <1:2> <arenaName>"));
        }

        if (args[1].length() > 1 && !args[1].chars().allMatch(Character::isDigit)) {
            throw new CommandException(Collections.singletonList("You need to specify which spawn (1 or 2)."));
        }

        if (!ManagerHandler.getArenaManager().isArena(args[2])) {
            throw new CommandException(Collections.singletonList("That arena does not exist."));
        }

        Player player = (Player) sender;
        Integer selection;

        try {
            selection = Integer.valueOf(args[1]);
        }
        catch(Exception e) {
            throw new CommandException(Collections.singletonList("The selection must be of 1 or 2."));
        }

        if (selection == 1) {
            ManagerHandler.getArenaManager().getArena(args[2]).setLocation1(player.getLocation());
        }
        else if (selection == 2) {
            ManagerHandler.getArenaManager().getArena(args[2]).setLocation2(player.getLocation());
        }
        else {
            throw new CommandException(Collections.singletonList("The selection must be of 1 or 2."));
        }

        sender.sendMessage(ChatColor.GREEN + "Modified spawn (" + selection + ") of arena (" + args[2] + ")");
        ManagerHandler.getArenaManager().getArena(args[2]).save(player);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> completions = new ArrayList<>();

        if (args.length == 2) {
            for (String name : ManagerHandler.getArenaManager().getArenas().keySet()) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(name);
                }
            }
        }
        else {
            for (String name : ManagerHandler.getArenaManager().getArenas().keySet()) {
                completions.add(name);
            }
        }

        return completions;
    }

}