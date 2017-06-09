package me.joeleoli.practice.scoreboard;

import org.bukkit.entity.Player;

import java.util.List;

public interface SidebarProvider {

	List<SidebarEntry> getLines(Player player);

}