package me.joeleoli.practice.data;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.connection.ConnectionPoolManager;
import me.joeleoli.practice.data.data.DatabaseCredentials;
import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.data.runnable.QueryCallback;
import me.joeleoli.practice.data.runnable.QueryRunnable;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.game.cache.CachedMatch;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class StorageBackend {

    @Getter private ConnectionPoolManager poolManager;

    public StorageBackend(DatabaseCredentials credentials) {
        this.poolManager = new ConnectionPoolManager(credentials);
        this.createTables();
    }

    public void closeConnections() {
        this.poolManager.closePool();
    }

    private synchronized void createTables() {
        new BukkitRunnable() {
            public void run() {
                Connection connection = null;

                try {
                    connection = poolManager.getConnection();
                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `practice_profiles` (`id` INT(11) NOT NULL AUTO_INCREMENT, `player_name` VARCHAR(16) NOT NULL, `player_uuid` VARCHAR(36) NOT NULL, `playtime` BIGINT(20) NOT NULL DEFAULT '0', `ranked_wins` INT(11) NOT NULL DEFAULT '0', `ranked_losses` INT(11) NOT NULL DEFAULT '0', `unranked_wins` INT(11) NOT NULL DEFAULT '0', `unranked_losses` INT(11) NOT NULL DEFAULT '0', `matches_played` INT(11) NOT NULL DEFAULT '0', `global_rating` INT(11) NOT NULL DEFAULT '1000', PRIMARY KEY (`id`), UNIQUE (`player_uuid`));").executeUpdate();
                    connection.prepareStatement("CREATE TABLE IF NOT EXISTS `practice_matches` (`id` int(11) NOT NULL AUTO_INCREMENT, `match_uuid` varchar(36) NOT NULL, `winner_name` varchar(16) NOT NULL, `winner_uuid` varchar(36) NOT NULL, `loser_name` varchar(16) NOT NULL, `loser_uuid` varchar(36) NOT NULL, `ladder` varchar(64) NOT NULL, `competitive` varchar(32) NOT NULL, `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, `elo_change` int(5) NOT NULL, PRIMARY KEY (`id`));").executeUpdate();
                }
                catch (SQLException e) {
                    if (!e.getMessage().contains("already exists")) {
                        PracticePlugin.getInstance().getLogger().severe("Failed createTables");
                        e.printStackTrace();
                    }
                }
                finally {
                    poolManager.close(connection, null, null);
                }
            }
        }.runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void addColumn(String ladder_name) {
        new BukkitRunnable() {
            public void run() {
                Connection connection = null;

                try {
                    connection = poolManager.getConnection();
                    connection.prepareStatement("ALTER TABLE `practice_profiles` ADD `" + ladder_name.toLowerCase() + "_rating` INT(11) NOT NULL DEFAULT '1000';").executeUpdate();
                }
                catch (SQLException e) {
                    if (!e.getMessage().contains("Duplicate column name")) {
                        PracticePlugin.getInstance().getLogger().severe("Failed addColumn");
                        e.printStackTrace();
                    }
                }
                finally {
                    poolManager.close(connection, null, null);
                }
            }
        }.runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void dropColumn(String ladder_name) {
        new BukkitRunnable() {
            public void run() {
                Connection connection = null;

                try {
                    connection = poolManager.getConnection();
                    connection.prepareStatement("ALTER TABLE `practice_profiles` DROP COLUMN `" + ladder_name.toLowerCase() + "_rating`;").executeUpdate();
                }
                catch (SQLException e) {
                    PracticePlugin.getInstance().getLogger().severe("Failed dropColumn");
                    e.printStackTrace();
                }
                finally {
                    poolManager.close(connection, null, null);
                }
            }
        }.runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void createProfile(OfflinePlayer player) {
        new QueryRunnable("SELECT `player_name` FROM `practice_profiles` WHERE `player_uuid`='" + player.getUniqueId().toString() + "' LIMIT 1;", new QueryCallback<ResultSet, SQLException>() {
            @Override
            public void call(ResultSet result, SQLException thrown) {
                if (thrown == null && result != null) {
                    try {
                        if (!result.next()) {
                            new BukkitRunnable() {
                                public void run() {
                                    Connection connection = null;
                                    PreparedStatement statement = null;

                                    try {
                                        connection = poolManager.getConnection();

                                        statement = connection.prepareStatement("INSERT INTO `practice_profiles` (`player_name`, `player_uuid`) VALUES (?, ?)");
                                        statement.setString(1, player.getName());
                                        statement.setString(2, player.getUniqueId().toString());
                                        statement.executeUpdate();
                                        statement.close();
                                    }
                                    catch (SQLException e) {
                                        if (!e.getMessage().contains("Duplicate entry")) {
                                            e.printStackTrace();
                                        }
                                    }
                                    finally {
                                        poolManager.close(connection, statement, null);
                                    }
                                }
                            }.runTaskAsynchronously(PracticePlugin.getInstance());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void saveProfile(Player player, GenericCallback callback) {
        new BukkitRunnable() {
            public void run() {
                PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

                if (profile == null) {
                    PracticePlugin.getInstance().getLogger().severe("Failed saveProfile (no data) -> " + player.getName());
                    return;
                }

                Connection connection = null;
                PreparedStatement statement = null;

                try {
                    Integer ratingsCount = 0;
                    String ratingsList = "";

                    if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
                        for (String ladder : ManagerHandler.getLadderManager().getLadders().keySet()) {
                            ratingsCount++;
                            ratingsList += ", `" + ladder.toLowerCase() + "_rating`=?";
                        }
                    }

                    connection = poolManager.getConnection();
                    statement = connection.prepareStatement("UPDATE `practice_profiles` SET `player_name`=?, `playtime`=?, `ranked_wins`=?, `ranked_losses`=?, `unranked_wins`=?, `unranked_losses`=?, `matches_played`=?, `global_rating`=?" + ratingsList + " WHERE `player_uuid`=?");
                    statement.setString(1, player.getName());
                    statement.setLong(2, ManagerHandler.getPlaytimeManager().getTotalPlayTime(player.getUniqueId()));
                    statement.setInt(3, profile.getRankedWins());
                    statement.setInt(4, profile.getRankedLosses());
                    statement.setInt(5, profile.getUnrankedWins());
                    statement.setInt(6, profile.getUnrankedLosses());
                    statement.setInt(7, profile.getMatchesPlayed());
                    statement.setInt(8, profile.getGlobalRating());

                    if (ratingsCount > 0) {
                        int i = 0;

                        for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                            i++;
                            statement.setInt(8 + i, profile.getLadderRatings().get(ladder).toInteger());
                        }
                    }

                    statement.setString(9 + ratingsCount, player.getUniqueId().toString());
                    statement.executeUpdate();
                    statement.close();
                }
                catch (SQLException e) {
                    PracticePlugin.getInstance().getLogger().severe("Failed saveProfile (exception) -> " + player.getName());
                    e.printStackTrace();

                    if (callback != null) {
                        callback.call(false);
                    }
                }
                finally {
                    poolManager.close(connection, statement, null);

                    if (callback != null) {
                        callback.call(true);
                    }
                }
            }
        }.runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void saveMatch(CachedMatch match) {
        new BukkitRunnable() {
            public void run() {
                Connection connection = null;
                PreparedStatement statement = null;

                try {
                    connection = poolManager.getConnection();
                    statement = connection.prepareStatement("INSERT INTO `practice_matches` (`match_uuid`, `winner_name`, `winner_uuid`, `loser_name`, `loser_uuid`, `ladder`, `competitive`, `timestamp`, `elo_change`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    statement.setString(1, match.getIdentifier().toString());
                    statement.setString(2, match.getWinner());
                    statement.setString(3, match.getWinnerUuid().toString());
                    statement.setString(4, match.getLoser());
                    statement.setString(5, match.getLoserUuid().toString());
                    statement.setString(6, match.getLadder());
                    statement.setString(7, (match.isRanked() ? "Ranked" : "Unranked"));
                    statement.setTimestamp(8, match.getFinishTimestamp());
                    statement.setInt(9, match.getEloChange());
                    statement.executeUpdate();
                }
                catch (SQLException e) {
                    PracticePlugin.getInstance().getLogger().severe("Failed saveMatch");
                    e.printStackTrace();
                }
                finally {
                    poolManager.close(connection, statement, null);
                }
            }
        }.runTaskAsynchronously(PracticePlugin.getInstance());
    }

    public synchronized void updateGlobalRanks() {
        new QueryRunnable("SELECT player_uuid, @curRank := @curRank + 1 AS rank FROM practice_profiles, (SELECT @curRank := 0) r ORDER BY global_rating DESC;", new QueryCallback<ResultSet, SQLException>() {
            @Override
            public void call(ResultSet result, SQLException thrown) {
                if (thrown == null && result != null) {
                    try {
                        while (result.next()) {
                            int rank = result.getInt("rank");

                            if (rank > 500) {
                                return;
                            }

                            UUID uuid = UUID.fromString(result.getString("player_uuid"));

                            Cache.rankings.put(uuid, rank);
                        }
                    }
                    catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).runTaskAsynchronously(PracticePlugin.getInstance());
    }

}