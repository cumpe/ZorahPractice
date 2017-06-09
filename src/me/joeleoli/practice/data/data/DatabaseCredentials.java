package me.joeleoli.practice.data.data;

import lombok.Getter;

public class DatabaseCredentials {
    
    @Getter private String hostname;
    @Getter private int port;
    @Getter private String username;
    @Getter private String password;
    @Getter private String databaseName;
    
    public DatabaseCredentials(String hostname, int port, String username, String password, String databaseName) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.databaseName = databaseName;
    }

}