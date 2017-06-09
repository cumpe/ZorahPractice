package me.joeleoli.practice.manager.type;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.GameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class SpectateManager implements Listener {

    public SpectateManager() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    public void startSpectating(Player player, Player target) {
        PracticeProfile practiceProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (practiceProfile.getStatus() == PlayerStatus.PLAYING || practiceProfile.getStatus() == PlayerStatus.SPECTATING) {
            player.sendMessage(ChatColor.RED + "You cannot spectate somebody while in a match.");
            return;
        }

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is not online.");
            return;
        }

        PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(target);

        if (targetProfile.getStatus() != PlayerStatus.PLAYING) {
            player.sendMessage(ChatColor.RED + "That player is not in a match.");
            return;
        }

        IMatch match = targetProfile.getCurrentMatch();

        if (match == null) {
            player.sendMessage(ChatColor.RED + "That match is no longer available.");
            return;
        }

        if (practiceProfile.getStatus() == PlayerStatus.EDITING_KITS) {
            ManagerHandler.getKitEditManager().getEditKits().remove(player.getUniqueId());
        }

        practiceProfile.setStatus(PlayerStatus.SPECTATING);
        practiceProfile.setSpectatingMatch(match);

        GameUtils.resetPlayer(player);

        player.getInventory().setContents(GameUtils.getSpectatorInventory());
        player.updateInventory();
        player.setAllowFlight(true);
        player.teleport(target);

        ManagerHandler.getEntityHider().hideAllPlayers(player);

        for (Player p : match.getPlayers()) {
            ManagerHandler.getEntityHider().showEntity(player, p);
        }

        if (!player.hasPermission("network.staff")) {
            match.sendMessage(ChatColor.AQUA + player.getName() + ChatColor.GRAY + " has started spectating.");
        }
    }

    public void stopSpectating(Player player, boolean sendMsg) {
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        profile.getSpectatingMatch().getSpectators().remove(player.getUniqueId());
        profile.setStatus(PlayerStatus.LOBBY);
        profile.setSpectatingMatch(null);

        GameUtils.resetPlayer(player);

        player.getInventory().setContents(GameUtils.getLobbyInventory());
        player.updateInventory();
        player.setAllowFlight(false);
        player.setShowEntities(new ArrayList<>());

        ManagerHandler.getConfig().teleportToSpawn(player);
        ManagerHandler.getEntityHider().showAllPlayers(player);

        if (sendMsg) {
            player.sendMessage(ChatColor.RED + "You are no longer spectating the match.");
        }
    }

}