package me.joeleoli.practice.scoreboard;

import com.google.common.collect.Iterables;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerBoard {

	private AtomicBoolean removed = new AtomicBoolean(false);
	private Team members, enemies, neutrals;
	private BufferedObjective bufferedObjective;
	private Scoreboard scoreboard;
	private Player player;
	private SidebarProvider defaultProvider;
	private BukkitRunnable runnable;

	public PlayerBoard(Player player) {
		this.player = player;

		this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		this.bufferedObjective = new BufferedObjective(scoreboard);

		this.members = scoreboard.registerNewTeam("members");
		this.members.setPrefix(ChatColor.GREEN.toString());
		this.members.setCanSeeFriendlyInvisibles(true);

		this.enemies = scoreboard.registerNewTeam("enemies");
		this.enemies.setPrefix(ChatColor.RED.toString());

		this.neutrals = scoreboard.registerNewTeam("neutrals");
		this.neutrals.setPrefix(ChatColor.WHITE.toString());

		player.setScoreboard(scoreboard);
	}

	public void remove() {
		if (!this.removed.getAndSet(true) && scoreboard != null) {
			for (Team team : scoreboard.getTeams()) {
				team.unregister();
			}

			for (Objective objective : scoreboard.getObjectives()) {
				objective.unregister();
			}
		}
	}

	public Player getPlayer() {
		return this.player;
	}

	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}

	public void setSidebarVisible() {
		this.bufferedObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
	}

	public void setDefaultSidebar(SidebarProvider provider) {
		if (provider != this.defaultProvider) {
			this.defaultProvider = provider;

			if (this.runnable != null) {
				this.runnable.cancel();
			}

			if (provider == null) {
				this.scoreboard.clearSlot(DisplaySlot.SIDEBAR);
				return;
			}

			(this.runnable = new BukkitRunnable() {
				@Override
				public void run() {
					if (removed.get()) {
						cancel();
						return;
					}

					if (provider == defaultProvider) {
						updateObjective();
					}
				}
			}).runTaskTimerAsynchronously(PracticePlugin.getInstance(), 2L, 2L);
		}
	}

	private void updateObjective() {
		if (this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed");
		}

		SidebarProvider provider = this.defaultProvider;

		if (provider == null) {
			this.bufferedObjective.setVisible(false);
		}
		else {
			this.bufferedObjective.setTitle(PracticeConfiguration.SCOREBOARD_TITLE);
			this.bufferedObjective.setAllLines(provider.getLines(player));
			this.bufferedObjective.flip();
		}
	}

	public void addUpdate(Player target) {
		this.addUpdates(Collections.singleton(target));
	}

	public void addUpdates(Iterable<? extends Player> updates) {
		if (Iterables.size(updates) == 0) {
			return;
		}

		if (this.removed.get()) {
			throw new IllegalStateException("Cannot update whilst board is removed.");
		}

		PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player update : updates) {
					if (update == null || !update.isOnline()) {
						continue;
					}

					List<Team> removeFrom = new ArrayList<>();

					for (Team team : scoreboard.getTeams()) {
						if (team.hasPlayer(update)) {
							removeFrom.add(team);
						}
					}

					for (Team team : removeFrom) {
						team.removePlayer(update);
					}

					if (player.equals(update)) {
						if (!members.hasPlayer(update)) {
							members.addPlayer(update);
						}

						continue;
					}

					if (profile.getStatus() == PlayerStatus.PLAYING && profile.getCurrentMatch().getTeam(player).contains(update)) {
						members.addPlayer(update);
					}
					else if (profile.getStatus() == PlayerStatus.PLAYING && profile.getCurrentMatch().getOpponents(player).contains(update)) {
						enemies.addPlayer(update);
					}
					else if (profile.getParty() != null && profile.getParty().getPlayers().contains(update)) {
						members.addPlayer(update);
					}
					else {
						neutrals.addPlayer(update);
					}
				}
			}
		}.runTask(PracticePlugin.getInstance());
	}

}