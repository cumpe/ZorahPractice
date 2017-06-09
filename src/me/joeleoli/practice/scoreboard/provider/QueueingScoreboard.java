package me.joeleoli.practice.scoreboard.provider;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.SidebarEntry;
import me.joeleoli.practice.scoreboard.SidebarProvider;
import me.joeleoli.practice.util.TimeUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueueingScoreboard implements SidebarProvider {

    public List<SidebarEntry> getLines(Player player) {
        List<SidebarEntry> lines = new ArrayList<>();

        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getParty() != null) {
            return Collections.emptyList();
        }

        if (profile.getCurrentQueue() == null) {
            return Collections.emptyList();
        }

        String display = profile.getCurrentQueue().getName();

        lines.add(new SidebarEntry("", PracticeConfiguration.SCOREBOARD_SPACER_LARGE, PracticeConfiguration.SCOREBOARD_SPACER_LARGE));
        lines.add(new SidebarEntry(ChatColor.GREEN + "Searching for", ChatColor.GREEN + " match...", ""));
        lines.add((display.length() > 8 ? new SidebarEntry(ChatColor.GRAY + " » " + ChatColor.WHITE + display.substring(0, 8), display.substring(8, display.length()), "") : new SidebarEntry(ChatColor.GRAY + " » " + ChatColor.WHITE + display)));
        lines.add(new SidebarEntry(ChatColor.GRAY + " » ", ChatColor.WHITE + (profile.getCurrentQueue().isRanked() ? "Ranked" : "Unranked"), ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + " » ", ChatColor.WHITE + TimeUtil.formatElapsingNanoseconds(profile.getQueueData().getStart()), ""));
        lines.add(new SidebarEntry("", PracticeConfiguration.SCOREBOARD_SPACER_LARGE + ChatColor.RESET, PracticeConfiguration.SCOREBOARD_SPACER_LARGE));

        return lines;
    }

}