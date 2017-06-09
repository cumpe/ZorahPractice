package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.game.match.type.TeamMatch;
import me.joeleoli.practice.game.party.PartyStatus;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PartyDuelCommand extends PluginCommand {

    public PartyDuelCommand(Plugin plugin) {
        super(plugin);
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
        if (!(sender instanceof Player)) throw new CommandException(Collections.singletonList("You must be a player to execute this command."));

        Player player = (Player) sender;
        PracticeProfile playerProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Use the party list menu to send party duel requests.");
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length > 2) {
                throw new CommandException(Collections.singletonList("You didn't specify a party to play."));
            }

            if (playerProfile.getParty() == null) {
                throw new CommandException(Collections.singletonList("You do not have a party."));
            }

            if (playerProfile.getParty().getStatus() != PartyStatus.IDLE) {
                throw new CommandException(Collections.singletonList("Your party is either in a queue or playing already."));
            }

            if (Bukkit.getPlayer(args[1]) == null) {
                throw new CommandException(Collections.singletonList("That party is no longer available."));
            }

            PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(Bukkit.getPlayer(args[1]));

            if (targetProfile.getParty() == null) {
                throw new CommandException(Collections.singletonList("That party is no longer available."));
            }

            if (!playerProfile.getParty().hasRequest(targetProfile.getParty())) {
                throw new CommandException(Collections.singletonList("That party has not sent you a duel request."));
            }

            if (targetProfile.getParty().getStatus() != PartyStatus.IDLE) {
                throw new CommandException(Collections.singletonList("That party is currently busy."));
            }

            playerProfile.getParty().sendMessage(ChatColor.GRAY + "Your party has accepted " + ChatColor.AQUA + targetProfile.getPlayer().getName() + "'s" + ChatColor.GRAY + " party duel request.");
            targetProfile.getParty().sendMessage(player.getName() + "'s party has accepted your party duel request.");

            TeamMatch match = new TeamMatch(null, playerProfile.getParty().getRequest(targetProfile.getParty()), ManagerHandler.getArenaManager().getRandomArena(), MatchType.PARTY_VERSUS_PARTY, targetProfile.getParty().getPlayers(), playerProfile.getParty().getPlayers());
            ManagerHandler.getMatchManager().getMatches().put(match.getIdentifier(), match);

            playerProfile.getParty().removeRequest(targetProfile.getParty());
        } else {
            player.sendMessage(ChatColor.RED + "Could not find that sub-command.");
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