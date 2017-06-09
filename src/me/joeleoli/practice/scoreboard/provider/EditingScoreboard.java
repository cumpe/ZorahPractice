package me.joeleoli.practice.scoreboard.provider;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.scoreboard.SidebarEntry;
import me.joeleoli.practice.scoreboard.SidebarProvider;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class EditingScoreboard implements SidebarProvider {

    public List<SidebarEntry> getLines(Player player) {
        List<SidebarEntry> lines = new ArrayList<>();

        lines.add(new SidebarEntry("", PracticeConfiguration.SCOREBOARD_SPACER_LARGE, PracticeConfiguration.SCOREBOARD_SPACER_LARGE));
        lines.add(new SidebarEntry(ChatColor.GRAY + "Use the anvil ", ChatColor.GRAY + "to save,", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "load, and dele", ChatColor.GRAY + "te your", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "kits.", "" + ChatColor.RESET, ""));
        lines.add(new SidebarEntry("", "", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "Once you are", ChatColor.GRAY + " finished", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "with your kits", ChatColor.GRAY + ", use", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "the sign to ", ChatColor.GRAY + "return to", ""));
        lines.add(new SidebarEntry(ChatColor.GRAY + "spawn."));
        lines.add(new SidebarEntry("", PracticeConfiguration.SCOREBOARD_SPACER_LARGE + ChatColor.RESET, PracticeConfiguration.SCOREBOARD_SPACER_LARGE));

        return lines;
    }

}