package me.joeleoli.practice.game.queue;

import lombok.Getter;
import lombok.Setter;

public class QueueData {

    @Getter private Object object;
    @Getter private IQueue queue;
    @Getter private Long start;
    @Getter @Setter private int rating, maxRange, minRange;

    public QueueData(Object object, IQueue queue, Integer rating) {
        this.object = object;
        this.queue = queue;
        this.start = System.nanoTime();
        this.rating = rating;
        this.maxRange = rating + 100;
        this.minRange = rating - 100;
    }

    public void incrementRange() {
        if (this.maxRange + 100 >= 2000) {
            this.maxRange = 2000;
        }
        else {
            this.maxRange = this.maxRange + 100;
        }

        if (this.minRange - 100 <= 0) {
            this.minRange = 0;
        }
        else {
            this.minRange = this.minRange - 100;
        }
    }

}