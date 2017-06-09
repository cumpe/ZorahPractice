package me.joeleoli.practice.command.type.variable;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.command.type.variable.argument.VariableHelpArgument;
import me.joeleoli.practice.command.type.variable.argument.VariableResetRatingArgument;

import me.joeleoli.practice.command.type.variable.argument.VariableSetRatingArgument;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class VariableCommand extends PluginCommand {

    private Map<String, PluginCommandArgument> commandArguments = new HashMap<>();

    public VariableCommand(Plugin plugin) {
        super(plugin);

        this.commandArguments.put("help", new VariableHelpArgument());
        this.commandArguments.put("setrating", new VariableSetRatingArgument());
        this.commandArguments.put("resetrating", new VariableResetRatingArgument());
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

        if (argument == null) {
            argument = this.commandArguments.get("help");
        }
        else {
            if(argument.requiresPlayer() && !(sender instanceof Player)) {
                throw new CommandException(Collections.singletonList("You must be a player to execute that command."));
            }

            if(argument.requiresPermission() && !sender.hasPermission(argument.getPermission())) {
                throw new CommandException(Collections.singletonList("You don't have permission to execute that command."));
            }
        }

        argument.onCommand(sender, args);
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