package me.joeleoli.practice.manager.type;

import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.file.FileConfig;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayTimeManager implements Listener {
	
	private TObjectLongMap<UUID> totalPlaytimeMap;
	private TObjectLongMap<UUID> sessionTimestamps;
	private FileConfig config;

	public PlayTimeManager() {
		this.totalPlaytimeMap = new TObjectLongHashMap<>();
		this.sessionTimestamps = new TObjectLongHashMap<>();
		this.config = new FileConfig("play-times.yml");
		this.reloadPlaytimeData();

		Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		this.sessionTimestamps.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();

		this.totalPlaytimeMap.put(uuid, this.getTotalPlayTime(uuid));
		this.sessionTimestamps.remove(uuid);
	}

	public void reloadPlaytimeData() {
		Object object = this.config.getConfig().get("playing-times");

		if (object instanceof MemorySection) {
			MemorySection section = (MemorySection) object;

			for (Object id : section.getKeys(false)) {
				this.totalPlaytimeMap.put(UUID.fromString((String) id), this.config.getConfig().getLong("playing-times." + id, 0L));
			}
		}

		long millis = System.currentTimeMillis();

		for (Player target : Bukkit.getOnlinePlayers()) {
			this.sessionTimestamps.put(target.getUniqueId(), millis);
		}
	}

	public void saveData() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			this.totalPlaytimeMap.put(player.getUniqueId(), this.getTotalPlayTime(player.getUniqueId()));
		}

		this.totalPlaytimeMap.forEachEntry((uuid, l) -> {
			this.config.getConfig().set("playing-times." + uuid.toString(), l);
			return true;
		});

		this.config.save();
	}

	public long getSessionPlayTime(UUID uuid) {
		long session = this.sessionTimestamps.get(uuid);
		return (session != this.sessionTimestamps.getNoEntryValue()) ? (System.currentTimeMillis() - session) : 0L;
	}

	public long getPreviousPlayTime(UUID uuid) {
		long stamp = this.totalPlaytimeMap.get(uuid);
		return (stamp == this.totalPlaytimeMap.getNoEntryValue()) ? 0L : stamp;
	}

	public long getTotalPlayTime(UUID uuid) {
		return this.getSessionPlayTime(uuid) + this.getPreviousPlayTime(uuid);
	}

}