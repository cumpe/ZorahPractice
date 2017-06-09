package me.joeleoli.practice.game.duel.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.listener.InventoryListener;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.menu.DuelMenu;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DuelPlayerArgument extends PluginCommandArgument {

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

        if (args[0].equalsIgnoreCase(player.getName())) {
            throw new CommandException(Collections.singletonList("You cannot duel yourself."));
        }

        if (playerProfile.getParty() != null) {
            throw new CommandException(Collections.singletonList("You cannot send a duel request while in a party."));
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            throw new CommandException(Collections.singletonList("That player is not online."));
        }

        PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(target);

        if (targetProfile.getStatus() == PlayerStatus.PLAYING) {
            throw new CommandException(Collections.singletonList("That player is currently in a match."));
        }

        if (targetProfile.hasRequest(player)) {
            throw new CommandException(Collections.singletonList("You have already sent that player a duel request."));
        }

        InventoryListener.selectedPlayer.put(player.getUniqueId(), target.getUniqueId());

        DuelMenu.open(player);
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 0) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        }
        else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(p.getName());
                }
            }
        }

        return completions;
    }

}