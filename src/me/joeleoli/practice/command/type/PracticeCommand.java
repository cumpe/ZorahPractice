package me.joeleoli.practice.command.type;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;

public class PracticeCommand extends PluginCommand {

    public PracticeCommand(Plugin plugin) {
        super(plugin);
    }
    
    @Override
    public boolean requiresPermission() {
        return true;
    }
    
    @Override
    public String getPermission() {
        return "practice.admin";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }
    
    @Override
    protected void onCommand(CommandSender sender, String[] args) throws CommandException {
        Player player = (Player)sender;

        if (args.length == 0) {
            this.help(player);
            return;
        }

        if (args[0].equalsIgnoreCase("setspawn")) {
            ManagerHandler.getConfig().setSpawnPoint(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "You have updated the spawn point.");
        }
        else if (args[0].equalsIgnoreCase("seteditor")) {
            ManagerHandler.getConfig().setEditorPoint(player.getLocation());
            player.sendMessage(ChatColor.GREEN + "You have updated the editor spawn point.");
        }
        else if (args[0].equalsIgnoreCase("spawn")) {
            ManagerHandler.getConfig().teleportToSpawn(player);
            player.sendMessage(ChatColor.YELLOW + "You have been teleported to the spawn point.");
        }
        else if (args[0].equalsIgnoreCase("editor")) {
            ManagerHandler.getConfig().teleportToEditor(player);
            player.sendMessage(ChatColor.YELLOW + "You have been teleported to the editor point.");
        }
        else if (args[0].equalsIgnoreCase("reload")) {
            Bukkit.getPluginManager().disablePlugin(PracticePlugin.getInstance());
            Bukkit.getPluginManager().enablePlugin(PracticePlugin.getInstance());
        }
        else if (args[0].equalsIgnoreCase("debug")) {
            Bukkit.getLogger().severe(InventoryUtils.playerInventoryToString(player.getInventory()));
        }
        else if (args[0].equalsIgnoreCase("location")) {
            player.sendMessage("" + player.getLocation().getBlockX());
            player.sendMessage("" + player.getLocation().getBlockY());
            player.sendMessage("" + player.getLocation().getBlockZ());
            player.sendMessage("" + player.getLocation().getPitch());
            player.sendMessage("" + player.getLocation().getYaw());
        }
        else {
            this.help(player);
        }
    }
    
    private void help(Player player) {
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
        player.sendMessage(ChatColor.YELLOW + " Practice Help");
        player.sendMessage(ChatColor.YELLOW + "   /prac setspawn » " + ChatColor.WHITE + "Sets the spawn point.");
        player.sendMessage(ChatColor.YELLOW + "   /prac seteditor » " + ChatColor.WHITE + "Sets the editor point.");
        player.sendMessage(ChatColor.YELLOW + "   /prac spawn » " + ChatColor.WHITE + "Teleports you to the spawn point.");
        player.sendMessage(ChatColor.YELLOW + "   /prac editor » " + ChatColor.WHITE + "Teleports you to the editor point.");
        player.sendMessage(ChatColor.YELLOW + "   /prac location » " + ChatColor.WHITE + "Shows your location.");
        player.sendMessage(ChatColor.YELLOW + "For additional help, use /practice help <subcommand>.");
        player.sendMessage(ChatColor.YELLOW + "" + ChatColor.STRIKETHROUGH + "---*--------------------------------------*---");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

}