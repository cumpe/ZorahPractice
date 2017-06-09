package me.joeleoli.practice.game.duel.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.duel.DuelRequest;
import me.joeleoli.practice.game.match.type.SoloMatch;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelAcceptArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

    public List<String> getAliases() {
        return this.aliases;
    }

    public boolean requiresPlayer() {
        return true;
    }

    public boolean requiresPermission() {
        return false;
    }

    public String getPermission() {
        return "";
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player)sender;
        PracticeProfile playerProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("Usage: /duel accept <player>"));
        }

        if (playerProfile.getStatus() != PlayerStatus.LOBBY) {
            throw new CommandException(Collections.singletonList("You must be in the lobby and not queueing to accept a duel."));
        }

        if (playerProfile.getParty() != null) {
            throw new CommandException(Collections.singletonList("You cannot have a party while accepting duel requests."));
        }

        Player target = Bukkit.getPlayer(args[1]);

        if (target == null) {
            throw new CommandException(Collections.singletonList("That player is not online."));
        }

        PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(target);

        if (!playerProfile.hasRequest(target)) {
            throw new CommandException(Collections.singletonList("That player has not sent you a duel request."));
        }

        if (targetProfile.getStatus() != PlayerStatus.LOBBY) {
            throw new CommandException(Collections.singletonList("That player is currently busy."));
        }

        if (targetProfile.getParty() != null) {
            throw new CommandException(Collections.singletonList("That player is currently busy."));
        }

        DuelRequest request = playerProfile.getRequest(target);
        playerProfile.removeRequest(target);

        Arena arena = ManagerHandler.getArenaManager().getRandomArena();

        if (arena == null) {
            throw new CommandException(Collections.singletonList("We could not find an available arena."));
        }

        new SoloMatch(null, request.getLadder(), arena, false, target, player);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}