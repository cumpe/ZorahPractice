package me.joeleoli.practice.game.arena.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaDeleteArgument extends PluginCommandArgument {

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
        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You provided too few arguments."));
        }

        if (!ManagerHandler.getArenaManager().isArena(args[1])) {
            throw new CommandException(Collections.singletonList("That arena does not exist."));
        }

        if (!args[1].chars().allMatch(Character::isLetter)) {
            throw new CommandException(Collections.singletonList("You provided an invalid character, only letters are accepted."));
        }

        sender.sendMessage(ChatColor.GREEN + "Successfully wiped arena (" + args[1] + ")");
        ManagerHandler.getArenaManager().getArena(args[1]).remove();
        ManagerHandler.getArenaManager().removeArena(args[1]);
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