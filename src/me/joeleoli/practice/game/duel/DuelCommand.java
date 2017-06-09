package me.joeleoli.practice.game.duel;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.duel.argument.DuelAcceptArgument;
import me.joeleoli.practice.game.duel.argument.DuelHelpArgument;
import me.joeleoli.practice.game.duel.argument.DuelPlayerArgument;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DuelCommand extends PluginCommand {

    private Map<String, PluginCommandArgument> commandArguments;

    public DuelCommand(Plugin plugin) {
        super(plugin);

        this.commandArguments = new HashMap<>();
        this.commandArguments.put("help", new DuelHelpArgument());
        this.commandArguments.put("accept", new DuelAcceptArgument());
        this.commandArguments.put("player", new DuelPlayerArgument());
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
        if (args.length == 0) {
            this.commandArguments.get("help").onCommand(sender, args);
        }
        else {
            if (this.commandArguments.containsKey(args[0].toLowerCase())) {
                this.commandArguments.get(args[0].toLowerCase()).onCommand(sender, args);
            }
            else {
                this.commandArguments.get("player").onCommand(sender, args);
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equals("")) {
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        }
        else {
            ArrayList<String> returnList = new ArrayList<>();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    returnList.add(p.getName());
                }
            }

            return returnList;
        }
    }

}