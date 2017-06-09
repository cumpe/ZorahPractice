package me.joeleoli.practice.game.ladder.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LadderSetAllowHealArgument extends PluginCommandArgument {

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
        if (args.length < 3) {
            throw new CommandException(Collections.singletonList("You provided too few arguments."));
        }

        if (!ManagerHandler.getLadderManager().getLadders().containsKey(args[1])) {
            throw new CommandException(Collections.singletonList("That ladder does not exist."));
        }

        try {
            Boolean value = Boolean.valueOf(args[2]);
            ManagerHandler.getLadderManager().getLadders().get(args[1]).allowHeal(value);
            ManagerHandler.getLadderManager().getLadders().get(args[1]).save(sender);
        }
        catch(Exception e) {
            throw new CommandException(Collections.singletonList("That value only accepts booleans."));
        }
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        ArrayList<String> returnList = new ArrayList<>();

        if (args.length == 2) {
            for (String name : ManagerHandler.getLadderManager().getLadders().keySet()) {
                if (name.toLowerCase().startsWith(args[1].toLowerCase())) {
                    returnList.add(name);
                }
            }
        }
        else {
            for (String name : ManagerHandler.getLadderManager().getLadders().keySet()) {
                returnList.add(name);
            }
        }

        return returnList;
    }

}