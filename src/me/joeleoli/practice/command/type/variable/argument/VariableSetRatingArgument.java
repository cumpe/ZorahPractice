package me.joeleoli.practice.command.type.variable.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;

import me.joeleoli.practice.util.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class VariableSetRatingArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.singletonList("sr");

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

    public VariableSetRatingArgument() {
        this.aliases = Collections.singletonList("sr");
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new CommandException(Collections.singletonList("Usage: /var sr <player> <ladder> <elo>"));
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null) {
            throw new CommandException(Collections.singletonList("That player is not registered."));
        }

        Ladder ladder = ManagerHandler.getLadderManager().getLadderByName(args[2]);

        if (ladder == null) {
            throw new CommandException(Collections.singletonList("That ladder does not exist."));
        }

        if (!args[3].chars().allMatch(Character::isDigit)) {
            throw new CommandException(Collections.singletonList("You must specify an integer as the ELO argument."));
        }

        int rating = Integer.valueOf(args[3]);

        MessageUtils.sendStaffMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "[" + sender.getName() + ": Set " + ChatColor.GREEN + ChatColor.ITALIC + target.getName() + ChatColor.GRAY + ChatColor.ITALIC + "'s " + ladder.getName() + " rating to " + ChatColor.GREEN + ChatColor.ITALIC + rating + ChatColor.GRAY + ChatColor.ITALIC + "]");

        DataAccessor.setRating(target.getUniqueId(), ladder, rating, new GenericCallback() {
            @Override
            public void call(boolean result) {
                if (result) {
                    sender.sendMessage(ChatColor.GREEN + "✓ Set player's rating.");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "✗ Failed to set player's rating.");
                }
            }
        });
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}