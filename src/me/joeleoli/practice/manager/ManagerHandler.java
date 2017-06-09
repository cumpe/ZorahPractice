package me.joeleoli.practice.manager;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import lombok.Getter;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.data.StorageBackend;
import me.joeleoli.practice.data.data.DatabaseCredentials;
import me.joeleoli.practice.manager.type.*;
import me.joeleoli.practice.util.EntityHider;

import org.bukkit.plugin.Plugin;

public class ManagerHandler {

    @Getter private static PracticeConfiguration config;
    @Getter private static StorageBackend storageBackend;
    @Getter private static PlayerManager playerManager;
    @Getter private static PartyManager partyManager;
    @Getter private static PlayTimeManager playtimeManager;
    @Getter private static LadderManager ladderManager;
    @Getter private static ArenaManager arenaManager;
    @Getter private static MatchManager matchManager;
    @Getter private static QueueManager queueManager;
    @Getter private static SpectateManager spectateManager;
    @Getter private static KitEditManager kitEditManager;
    @Getter private static ScoreboardHandler scoreboardHandler;
    @Getter private static ProtocolManager protocolManager;
    @Getter private static EntityHider entityHider;

    public ManagerHandler(Plugin plugin) {
        config = new PracticeConfiguration();
        storageBackend = new StorageBackend(new DatabaseCredentials(config.getRootConfig().getConfig().getString("database.host"), config.getRootConfig().getConfig().getInt("database.port"), config.getRootConfig().getConfig().getString("database.user"), config.getRootConfig().getConfig().getString("database.pass"), config.getRootConfig().getConfig().getString("database.dbName")));
        playerManager = new PlayerManager();
        partyManager = new PartyManager();
        playtimeManager = new PlayTimeManager();
        ladderManager = new LadderManager();
        arenaManager = new ArenaManager();
        matchManager = new MatchManager();
        queueManager = new QueueManager();
        spectateManager = new SpectateManager();
        kitEditManager = new KitEditManager();
        scoreboardHandler = new ScoreboardHandler();
        protocolManager = ProtocolLibrary.getProtocolManager();
        entityHider = new EntityHider(plugin, EntityHider.Policy.BLACKLIST);
    }

    public static void saveData() {
        storageBackend.closeConnections();
        playerManager.saveData();
        playtimeManager.saveData();
        playerManager.saveData();
        matchManager.cancelMatches();
    }

}