package me.joeleoli.practice.scoreboard.provider;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.game.match.MatchStatus;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.SidebarEntry;
import me.joeleoli.practice.scoreboard.SidebarProvider;
import me.joeleoli.practice.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlayingScoreboard implements SidebarProvider {

    public List<SidebarEntry> getLines(Player player) {
        List<SidebarEntry> lines = new ArrayList<>();
        List<SidebarEntry> toReturn = new ArrayList<>();

        PracticeProfile practiceProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
        IMatch match = practiceProfile.getCurrentMatch();

        String timer = match.getMatchStatus() == MatchStatus.STARTING ? "Starting..." : TimeUtil.formatElapsingNanoseconds(match.getStartNano());
        String opponentName = "";

        if (match.getMatchType() == MatchType.ONE_VERSUS_ONE) {
            opponentName = ((Player)match.getOpponents(player).toArray()[0]).getName();

            lines.add(new SidebarEntry(ChatColor.GREEN + "Opponent"));
            lines.add(new SidebarEntry(opponentName));
            lines.add(new SidebarEntry(ChatColor.GREEN + "Duration"));
            lines.add(new SidebarEntry(timer));
        }
        else if (match.getMatchType() == MatchType.TWO_VERSUS_TWO) {
            lines.add(new SidebarEntry(ChatColor.GREEN + "Opponents Left"));
            lines.add(new SidebarEntry(match.getOpponentsLeft(player) + "/2"));
            lines.add(new SidebarEntry(ChatColor.GREEN + "Duration"));
            lines.add(new SidebarEntry(timer));
        }
        else if (match.getMatchType() == MatchType.PARTY_VERSUS_PARTY) {

            lines.add(new SidebarEntry(ChatColor.GREEN + "Opponents Left"));
            lines.add(new SidebarEntry(match.getOpponentsLeft(player) + "/" + match.getOpponents(player).size()));
            lines.add(new SidebarEntry(ChatColor.GREEN + "Duration"));
            lines.add(new SidebarEntry(timer));
        }

        if (practiceProfile.getEnderpearlTimer().isActive()) {
            DecimalFormat format = new DecimalFormat("##.#");
            lines.add(new SidebarEntry(ChatColor.RED + "Enderpearl"));
            lines.add(new SidebarEntry(format.format(practiceProfile.getEnderpearlTimer().getTimeLeft() / 1000.0).replace(",", ".") + "s"));
        }

        toReturn.add(new SidebarEntry((opponentName != null && opponentName.length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER), PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER));
        toReturn.addAll(lines);
        toReturn.add(new SidebarEntry((opponentName != null && opponentName.length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER), PracticeConfiguration.SCOREBOARD_SPACER + ChatColor.RESET, PracticeConfiguration.SCOREBOARD_SPACER));

        return toReturn;
    }

}