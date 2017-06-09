package me.joeleoli.practice.game.party;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.PluginCommandArgument;

import me.joeleoli.practice.game.party.argument.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class PartyCommand extends PluginCommand {

    private Map<String, PluginCommandArgument> commandArguments;

    public PartyCommand(Plugin plugin) {
        super(plugin);

        this.commandArguments = new HashMap<>();
        this.commandArguments.put("accept", new PartyAcceptArgument());
        this.commandArguments.put("create", new PartyCreateArgument());
        this.commandArguments.put("decline", new PartyDeclineArgument());
        this.commandArguments.put("disband", new PartyDisbandArgument());
        this.commandArguments.put("help", new PartyHelpArgument());
        this.commandArguments.put("info", new PartyInfoArgument());
        this.commandArguments.put("invite", new PartyInviteArgument());
        this.commandArguments.put("join", new PartyJoinArgument());
        this.commandArguments.put("kick", new PartyKickArgument());
        this.commandArguments.put("leave", new PartyLeaveArgument());
        this.commandArguments.put("list", new PartyListArgument());
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