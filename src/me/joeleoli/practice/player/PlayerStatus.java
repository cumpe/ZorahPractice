package me.joeleoli.practice.player;

public enum PlayerStatus {

    LOBBY("Lobby"),
    QUEUEING("Queueing"),
    PLAYING("Playing"),
    SPECTATING("Spectating"),
    EDITING_KITS("Editing Kits"),
    FFA("Playing FFA");

    private String toString;

    PlayerStatus(String toString) {
        this.toString = toString;
    }

    @Override
    public String toString() {
        return this.toString;
    }

}