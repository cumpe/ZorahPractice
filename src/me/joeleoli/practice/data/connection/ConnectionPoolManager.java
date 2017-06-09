package me.joeleoli.practice.data.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.data.DatabaseCredentials;

import org.bukkit.Bukkit;

import java.sql.*;

public class ConnectionPoolManager {

    private HikariDataSource dataSource;
    private DatabaseCredentials credentials;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;
    
    public ConnectionPoolManager(DatabaseCredentials credentials) {
        this.credentials = credentials;
        
        this.init();
        this.setupPool();
    }
    
    private void init() {
        this.minimumConnections = 0;
        this.maximumConnections = 25;
        this.connectionTimeout = 10000L;
    }
    
    private void setupPool() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:mysql://" + this.credentials.getHostname() + ":" + this.credentials.getPort() + "/" + this.credentials.getDatabaseName());
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(this.credentials.getUsername());
        config.setPassword(this.credentials.getPassword());
        config.setMinimumIdle(this.minimumConnections);
        config.setMaximumPoolSize(this.maximumConnections);
        config.setConnectionTimeout(this.connectionTimeout);

        try {
            this.dataSource = new HikariDataSource(config);
        }
        catch (Exception e) {
            Bukkit.getLogger().severe("[ZorahPractice] - Unable to establish MySQL connection, plugin disabled.");
            Bukkit.getServer().getPluginManager().disablePlugin(PracticePlugin.getInstance());
        }
    }
    
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }
    
    public void close(Connection conn, Statement statement, ResultSet res) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {}
        }

        if (statement != null) {
            try {
                statement.close();
            }
            catch (SQLException ex2) {}
        }

        if (res != null) {
            try {
                res.close();
            }
            catch (SQLException ex3) {}
        }
    }
    
    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException ex) {}
        }

        if (ps != null) {
            try {
                ps.close();
            }
            catch (SQLException ex2) {}
        }

        if (res != null) {
            try {
                res.close();
            }
            catch (SQLException ex3) {}
        }
    }
    
    public void closePool() {
        if (this.dataSource != null && !this.dataSource.isClosed()) {
            this.dataSource.close();
        }
    }

}