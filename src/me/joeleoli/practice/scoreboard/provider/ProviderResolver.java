package me.joeleoli.practice.scoreboard.provider;

import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.SidebarEntry;
import me.joeleoli.practice.scoreboard.SidebarProvider;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderResolver implements SidebarProvider {

	private Map<PlayerStatus, SidebarProvider> providers = new HashMap<>();

	public ProviderResolver() {
		this.providers.put(PlayerStatus.LOBBY, new LobbyScoreboard());
		this.providers.put(PlayerStatus.EDITING_KITS, new EditingScoreboard());
		this.providers.put(PlayerStatus.PLAYING, new PlayingScoreboard());
		this.providers.put(PlayerStatus.QUEUEING, new QueueingScoreboard());
		this.providers.put(PlayerStatus.SPECTATING, new SpectatorScoreboard());
	}

	@Override
	public List<SidebarEntry> getLines(Player player) {
		PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

		if (profile.isHidingScoreboard()) {
			return Collections.emptyList();
		}

		if (this.providers.containsKey(profile.getStatus())) {
			return this.providers.get(profile.getStatus()).getLines(player);
		}
		else {
			return Collections.emptyList();
		}
	}

}