package me.joeleoli.practice.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.player.PlayerStatus;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class PacketListener {

	public PacketListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(PracticePlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.NAMED_SOUND_EFFECT) {
					public void onPacketSending(PacketEvent event) {
						PacketContainer packet = event.getPacket();
						String sound = packet.getStrings().read(0);
						World world = event.getPlayer().getWorld();

						if (sound.contains("random.successful_hit") || sound.contains("weather")) {
							event.setCancelled(false);
							return;
						}

						Player player = event.getPlayer();
						PracticeProfile playerData = ManagerHandler.getPlayerManager().getPlayerProfile(player);

						if (playerData.getStatus() != PlayerStatus.PLAYING) {
							event.setCancelled(true);
							return;
						}

						double x = (packet.getIntegers().read(0) / 8.0);
						double y = (packet.getIntegers().read(1) / 8.0);
						double z = (packet.getIntegers().read(2) / 8.0);
						Location loc = new Location(world, x, y, z);

						Player closest = null;
						double bestDistance = Double.MAX_VALUE;

						// Find the player closest to the sound
						for (Player p : world.getPlayers()) {
							double distance = p.getLocation().distance(loc);

							if (distance < bestDistance && ManagerHandler.getPlayerManager().getPlayerProfile(p).getStatus() == PlayerStatus.PLAYING) {
								bestDistance = distance;
								closest = p;
							}
						}

						if (closest != null) {
							if (!(playerData.getCurrentMatch().getPlayers().contains(closest))) {
								event.setCancelled(true);
							}
						}
					}
				});

		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(PracticePlugin.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Server.WORLD_EVENT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						if (event.getPacket().getIntegers().read(0) == 1002) {
							event.setCancelled(true);
						}
					}
				}
		);

	}

}