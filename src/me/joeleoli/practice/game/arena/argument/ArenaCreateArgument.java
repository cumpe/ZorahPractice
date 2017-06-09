package me.joeleoli.practice.game.arena.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArenaCreateArgument extends PluginCommandArgument {

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

        if (ManagerHandler.getArenaManager().isArena(args[1])) {
            throw new CommandException(Collections.singletonList("That arena already exists."));
        }

        if (!args[1].chars().allMatch(Character::isLetter)) {
            throw new CommandException(Collections.singletonList("You provided an invalid character, only letters are accepted."));
        }

        Arena arena = new Arena(args[1]);
        arena.save(sender);
        ManagerHandler.getArenaManager().addArena(args[1], arena);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}