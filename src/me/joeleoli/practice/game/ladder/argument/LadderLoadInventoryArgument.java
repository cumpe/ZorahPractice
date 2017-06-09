package me.joeleoli.practice.game.ladder.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LadderLoadInventoryArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.singletonList("loadinv");

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
        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You provided too few arguments."));
        }

        if (!ManagerHandler.getLadderManager().getLadders().containsKey(args[1])) {
            throw new CommandException(Collections.singletonList("That ladder does not exist."));
        }

        Player player = (Player) sender;

        player.getInventory().setArmorContents(ManagerHandler.getLadderManager().getLadders().get(args[1]).getDefaultInventory().getArmorContents());
        player.getInventory().setContents(ManagerHandler.getLadderManager().getLadders().get(args[1]).getDefaultInventory().getContents());
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