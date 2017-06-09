package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.party.event.PlayerKickPlayerPartyEvent;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyKickArgument extends PluginCommandArgument {

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
            throw new CommandException(Collections.singletonList("You did not provide a player to kick."));
        }

        if (profile.getParty() == null) {
            throw new CommandException(Collections.singletonList("You do not have a party."));
        }

        if (profile.getParty().getLeader() != player) {
            throw new CommandException(Collections.singletonList("You must be the party leader to kick a player."));
        }

        if (player.getName().equalsIgnoreCase(args[1])) {
            throw new CommandException(Collections.singletonList("You cannot kick yourself."));
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            throw new CommandException(Collections.singletonList("That player is not online."));
        }

        if (!profile.getParty().getPlayers().contains(Bukkit.getPlayer(args[1]))) {
            throw new CommandException(Collections.singletonList("That player is not in your party."));
        }

        PlayerKickPlayerPartyEvent partyEvent = new PlayerKickPlayerPartyEvent(player, Bukkit.getPlayer(args[1]), profile.getParty(), true, true);
        Bukkit.getPluginManager().callEvent(partyEvent);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getParty() == null) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 0 || args[0].equalsIgnoreCase("")) {
            for (Player p : profile.getParty().getPlayers()) {
                completions.add(p.getName());
            }
        }
        else {
            for (Player p : profile.getParty().getPlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }

}