package me.joeleoli.practice.game.ladder.argument;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommandArgument;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerElo;
import me.joeleoli.practice.player.PlayerKits;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LadderCreateArgument extends PluginCommandArgument {

    private List<String> aliases = Collections.emptyList();

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

    public void onCommand(CommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            throw new CommandException(Collections.singletonList("You provided too few arguments."));
        }

        if (ManagerHandler.getLadderManager().getLadders().containsKey(args[1])) {
            throw new CommandException(Collections.singletonList("That ladder already exists."));
        }

        Ladder ladder = new Ladder(args[1]);
        ladder.save(sender);
        ManagerHandler.getLadderManager().getLadders().put(args[1], ladder);

        for (PracticeProfile data : ManagerHandler.getPlayerManager().getAllData().values()) {
            data.getLadderRatings().put(ladder, new PlayerElo(1000));
            data.getLadderKits().put(ladder, new PlayerKits());
        }

        ManagerHandler.getStorageBackend().addColumn(ladder.getName());
        ManagerHandler.getStorageBackend().addColumn(ladder.getName() + "_2v2");
    }

    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}