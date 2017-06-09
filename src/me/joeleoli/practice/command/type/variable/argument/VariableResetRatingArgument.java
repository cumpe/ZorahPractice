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

public class VariableResetRatingArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.singletonList("rr");

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

    public VariableResetRatingArgument() {
        this.aliases = Collections.singletonList("rr");
    }

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 3) {
            throw new CommandException(Collections.singletonList("Usage: /var rr <player> <ladder>"));
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null) {
            throw new CommandException(Collections.singletonList("That player is not registered."));
        }

        Ladder ladder = ManagerHandler.getLadderManager().getLadderByName(args[2]);

        if (ladder == null) {
            throw new CommandException(Collections.singletonList("That ladder does not exist."));
        }

        MessageUtils.sendStaffMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "[" + sender.getName() + ": Reset " + ChatColor.GREEN + ChatColor.ITALIC + target.getName() + ChatColor.GRAY + ChatColor.ITALIC + "'s " + ladder.getName() + " rating]");

        DataAccessor.setRating(target.getUniqueId(), ladder, 1000, new GenericCallback() {
            @Override
            public void call(boolean result) {
                if (result) {
                    sender.sendMessage(ChatColor.GREEN + "✓ Reset player's rating.");
                }
                else {
                    sender.sendMessage(ChatColor.RED + "✗ Failed to reset player's rating.");
                }
            }
        });
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}