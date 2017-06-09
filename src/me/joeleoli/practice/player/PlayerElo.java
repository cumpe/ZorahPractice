package me.joeleoli.practice.player;

public class PlayerElo {

    private int rating;

    public PlayerElo(int rating) {
        this.rating = rating;
    }

    public PlayerElo setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public int getNewRating(int opponentRating, double score) {
        double kFactor = 32;
        double expectedScore = getExpectedScore(this.rating, opponentRating);
        return calculateNewRating(this.rating, score, expectedScore, kFactor);
    }

    private int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    private double getExpectedScore (int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }

    public int toInteger() {
        return this.rating;
    }

}