package me.joeleoli.practice.game.party.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.party.event.PlayerJoinPartyEvent;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyAcceptArgument extends PluginCommandArgument {

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

        if (profile.getStatus() != PlayerStatus.LOBBY) {
            throw new CommandException(Collections.singletonList("You must be in the lobby to join a party."));
        }

        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You did not provide a party to join."));
        }

        if (Bukkit.getPlayer(args[1]) == null) {
            throw new CommandException(Collections.singletonList("That party doesn't exist."));
        }

        if (profile.getParty() != null) {
            throw new CommandException(Collections.singletonList("You already have a party."));
        }

        Party party = ManagerHandler.getPlayerManager().getPlayerProfile(Bukkit.getPlayer(args[1])).getParty();

        if (!party.hasInvite(player.getUniqueId())) {
            throw new CommandException(Collections.singletonList("You have not been invited to that party."));
        }

        PlayerJoinPartyEvent partyEvent = new PlayerJoinPartyEvent(player, party, true);
        Bukkit.getPluginManager().callEvent(partyEvent);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}