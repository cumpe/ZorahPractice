package me.joeleoli.practice.command.type;

import me.joeleoli.practice.command.CommandException;
import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.PingUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PingCommand extends PluginCommand {

    public PingCommand(Plugin plugin) {
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
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.GREEN + "Your Ping: " + PingUtil.getPing(player));
        }
        else {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online.");
                return;
            }

            player.sendMessage(ChatColor.GREEN + target.getName() + "'s Ping: " + PingUtil.getPing(target));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 0 || args[0].equals("")) {
            return Bukkit.getOnlinePlayers().stream().map(player -> player.getName()).collect(Collectors.toList());
        } else {
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