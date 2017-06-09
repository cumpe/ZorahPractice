package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyInviteArgument extends PluginCommandArgument {

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
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You did not provide a player to invite."));
        }

        if (profile.getParty() == null) {
            throw new CommandException(Collections.singletonList("You do not have a party."));
        }

        System.out.println(player.getUniqueId().toString());
        System.out.println(profile.getParty().getLeaderUuid().toString());

        if (profile.getParty().getLeader() != player) {
            throw new CommandException(Collections.singletonList("You must be the party leader to invite a player."));
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            throw new CommandException(Collections.singletonList("You cannot invite yourself."));
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            throw new CommandException(Collections.singletonList("That player is not online."));
        }

        if (profile.getParty().hasInvite(Bukkit.getPlayer(args[1]).getUniqueId())) {
            throw new CommandException(Collections.singletonList("That player is already invited."));
        }

        if (profile.getParty().hasPlayer(Bukkit.getPlayer(args[1]).getUniqueId())) {
            throw new CommandException(Collections.singletonList("That player is already in your party."));
        }

        profile.getParty().addInvite(Bukkit.getPlayer(args[1]).getUniqueId());
        profile.getParty().sendMessage(ChatColor.GRAY + args[1] + " has been invited to the party.");

        new FancyMessage("You have been invited to join ").color(ChatColor.GRAY)
                .then(player.getName() + "'s party").color(ChatColor.AQUA)
                .then(". Click ").color(ChatColor.GRAY)
                .then("[").color(ChatColor.GRAY).then("Accept")
                .color(ChatColor.GREEN).command("/party accept " + player.getName())
                .then("]").color(ChatColor.GRAY)
                .then(" or ").color(ChatColor.GRAY)
                .then("[").color(ChatColor.GRAY)
                .then("Decline").color(ChatColor.DARK_RED).command("/party decline " + player.getName())
                .then("]").color(ChatColor.GRAY).send(Bukkit.getPlayer(args[1]));
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}