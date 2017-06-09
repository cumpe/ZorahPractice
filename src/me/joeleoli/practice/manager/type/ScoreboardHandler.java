package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.scoreboard.PlayerBoard;
import me.joeleoli.practice.scoreboard.provider.ProviderResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardHandler implements Listener {

	@Getter private Map<UUID, PlayerBoard> playerBoards = new HashMap<>();
	private ProviderResolver timerSidebarProvider;

	public ScoreboardHandler() {
		Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
		this.timerSidebarProvider = new ProviderResolver();

		Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();

		for (Player player : players) {
			this.applyBoard(player).addUpdates(players);
		}
	}

	public PlayerBoard getPlayerBoard(UUID uuid) {
		return this.playerBoards.get(uuid);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.playerBoards.remove(event.getPlayer().getUniqueId()).remove();
	}

	public PlayerBoard applyBoard(Player player) {
		PlayerBoard board = new PlayerBoard(player);
		PlayerBoard previous = this.playerBoards.put(player.getUniqueId(), board);

		if (previous != null && previous != board) {
			previous.remove();
		}

		board.setSidebarVisible();
		board.setDefaultSidebar(this.timerSidebarProvider);

		return board;
	}

}