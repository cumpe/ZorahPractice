package me.joeleoli.practice.scoreboard.provider;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.SidebarEntry;
import me.joeleoli.practice.scoreboard.SidebarProvider;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyScoreboard implements SidebarProvider {

    public List<SidebarEntry> getLines(Player player) {
        PracticeProfile profile = DataAccessor.getPlayerProfile(player);
        List<SidebarEntry> lines = new ArrayList<>();

        lines.add(new SidebarEntry(player.getName().length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Username" + ChatColor.GRAY + ": " + ChatColor.WHITE, player.getName()));
        lines.add(new SidebarEntry(player.getName().length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER + ChatColor.BLUE, PracticeConfiguration.SCOREBOARD_SPACER));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Global Rank", ChatColor.GRAY + ": " + ChatColor.WHITE + (Cache.rankings.containsKey(player.getUniqueId()) ? "#" + Cache.rankings.get(player.getUniqueId()) : "Over #500")));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Global ELO", ChatColor.GRAY + ": " + ChatColor.WHITE + profile.getGlobalRating()));
        lines.add(new SidebarEntry(player.getName().length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER + ChatColor.RED, PracticeConfiguration.SCOREBOARD_SPACER));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Online", ChatColor.GRAY + ": " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size()));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Fighting", ChatColor.GRAY + ": " + ChatColor.WHITE + Cache.playingAmount));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Queueing", ChatColor.GRAY + ": " + ChatColor.WHITE + Cache.queueingAmount));
        lines.add(new SidebarEntry("", ChatColor.AQUA + "Spectating", ChatColor.GRAY + ": " + ChatColor.WHITE + Cache.spectatingAmount));
        lines.add(new SidebarEntry(player.getName().length() >= 10 ? PracticeConfiguration.SCOREBOARD_SPACER_LARGE : PracticeConfiguration.SCOREBOARD_SPACER, PracticeConfiguration.SCOREBOARD_SPACER + ChatColor.RESET, PracticeConfiguration.SCOREBOARD_SPACER));

        return lines;
    }

}