package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.party.Party;

import me.joeleoli.practice.manager.ManagerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyDeclineArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return false;
    }

    public boolean requiresPermission() {
        return false;
    }

    public String getPermission() {
        return "";
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player) sender;

        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You did not provide a party to decline."));
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            throw new CommandException(Collections.singletonList("That party does not exist."));
        }

        Party party = ManagerHandler.getPlayerManager().getPlayerProfile(Bukkit.getPlayer(args[1])).getParty();

        if (!party.hasInvite(player.getUniqueId())) {
            throw new CommandException(Collections.singletonList("You have not been invited to that party."));
        }

        player.sendMessage(ChatColor.GRAY + "You have declined " + args[1] + "'s invite.");
        party.removeInvite(player.getUniqueId());
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}