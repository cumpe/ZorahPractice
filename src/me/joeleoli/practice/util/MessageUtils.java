package me.joeleoli.practice.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static void sendStaffMessage(String message) {
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.hasPermission("zorah.staff")) {
                p.sendMessage(message);
            }
        }
    }

}