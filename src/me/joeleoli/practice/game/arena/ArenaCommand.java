package me.joeleoli.practice.game.arena;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.arena.argument.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class ArenaCommand extends PluginCommand {

    private Map<String, PluginCommandArgument> commandArguments;

    public ArenaCommand(Plugin plugin) {
        super(plugin);

        this.commandArguments = new HashMap<>();
        this.commandArguments.put("help", new ArenaHelpArgument());
        this.commandArguments.put("create", new ArenaCreateArgument());
        this.commandArguments.put("delete", new ArenaDeleteArgument());
        this.commandArguments.put("setspawn", new ArenaSetSpawnArgument());
        this.commandArguments.put("list", new ArenaListArgument());
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
        return true;
    }

    @Override
    protected void onCommand(CommandSender sender, String[] args) throws CommandException {
        String solve = args.length == 0 ? "help" : args[0].toLowerCase();
        PluginCommandArgument argument = null;

        if (this.commandArguments.containsKey(solve)) {
            argument = this.commandArguments.get(solve);
        }
        else {
            for (Map.Entry<String, PluginCommandArgument> commandArgument : this.commandArguments.entrySet()) {
                if (commandArgument.getValue().getAliases().contains(solve)) {
                    argument = commandArgument.getValue();
                }
            }
        }

        if (argument == null) argument = this.commandArguments.get("help");

        try {
            if (argument.requiresPlayer() && !(sender instanceof Player)) throw new CommandException(Collections.singletonList("You must be a player to execute this argument."));
            argument.onCommand(sender, args);
        }
        catch(CommandException e) {
            throw e;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        String solve = args.length == 0 ? "help" : args[0].toLowerCase();
        PluginCommandArgument argument = null;

        if (this.commandArguments.containsKey(solve)) {
            argument = this.commandArguments.get(solve);
        }
        else {
            for (Map.Entry<String, PluginCommandArgument> commandArgument : this.commandArguments.entrySet()) {
                if (commandArgument.getValue().getAliases().contains(args[0].toLowerCase())) {
                    argument = commandArgument.getValue();
                }
            }
        }

        if (argument == null) argument = this.commandArguments.get("help");
        return argument.onTabComplete(sender, args);
    }

}