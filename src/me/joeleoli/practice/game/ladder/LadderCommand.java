package me.joeleoli.practice.game.ladder;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.PluginCommandArgument;

import me.joeleoli.practice.game.ladder.argument.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class LadderCommand extends PluginCommand {

    private Map<String, PluginCommandArgument> commandArguments;

    public LadderCommand(Plugin plugin) {
        super(plugin);

        this.commandArguments = new HashMap<>();
        this.commandArguments.put("create", new LadderCreateArgument());
        this.commandArguments.put("delete", new LadderDeleteArgument());
        this.commandArguments.put("help", new LadderHelpArgument());
        this.commandArguments.put("list", new LadderListArgument());
        this.commandArguments.put("loadinventory", new LadderLoadInventoryArgument());
        this.commandArguments.put("setallowedit", new LadderSetAllowEditArgument());
        this.commandArguments.put("setallowheal", new LadderSetAllowHealArgument());
        this.commandArguments.put("setallowhunger", new LadderSetAllowHungerArgument());
        this.commandArguments.put("setdisplayname", new LadderSetDisplayNameArgument());
        this.commandArguments.put("sethitdelay", new LadderSetHitDelayArgument());
        this.commandArguments.put("seticon", new LadderSetIconArgument());
        this.commandArguments.put("setinventory", new LadderSetInventoryArgument());
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
        if (args.length == 0) {
            return new ArrayList<>(this.commandArguments.keySet());
        }
        else {
            if (this.commandArguments.containsKey(args[0].toLowerCase())) {
                return this.commandArguments.get(args[0].toLowerCase()).onTabComplete(sender, args);
            }
            else {
                List<String> completions = new ArrayList<>();

                for (String key : this.commandArguments.keySet()) {
                    if (key.toLowerCase().startsWith(args[0].toLowerCase())) {
                        completions.add(key);
                    }
                }

                return completions;
            }
        }
    }

}