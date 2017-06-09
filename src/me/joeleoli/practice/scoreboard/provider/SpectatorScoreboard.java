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

import java.util.ArrayList;
import java.util.List;

public class SpectatorScoreboard implements SidebarProvider {

    public List<SidebarEntry> getLines(Player player) {
        List<SidebarEntry> lines = new ArrayList<>();

        PracticeProfile practiceProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
        IMatch match = practiceProfile.getCurrentMatch();

        String timer = match.getMatchStatus() == MatchStatus.STARTING ? "Starting..." : TimeUtil.formatElapsingNanoseconds(match.getStartNano());

        lines.add(new SidebarEntry(PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER));

        if (match.getMatchType() == MatchType.ONE_VERSUS_ONE) {
            lines.add(new SidebarEntry("", match.getPlayers().get(0).getName(), ""));
            lines.add(new SidebarEntry("", ChatColor.RED + "vs", ""));
            lines.add(new SidebarEntry("", match.getPlayers().get(1).getName(), ""));
            lines.add(new SidebarEntry("", ChatColor.WHITE + "", ""));
        }

        lines.add(new SidebarEntry(ChatColor.GREEN + "Duration"));
        lines.add(new SidebarEntry(timer));
        lines.add(new SidebarEntry(PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER + ChatColor.RESET, PracticeConfiguration.SCOREBOARD_SPACER));

        return lines;
    }

}