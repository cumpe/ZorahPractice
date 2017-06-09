package me.joeleoli.practice.game.queue;

import me.joeleoli.practice.game.ladder.Ladder;

import java.util.UUID;

public interface IQueue {

    UUID getIdentifier();

    String getName();

    boolean isRanked();

    int getPlayingAmount();

    int getQueueingAmount();

    void addToQueue(Object object);

    void removeFromQueue(Object object);

    Ladder getLadder();

}