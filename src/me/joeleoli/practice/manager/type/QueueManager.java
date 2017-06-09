package me.joeleoli.practice.manager.type;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.type.SoloQueue;
import me.joeleoli.practice.game.queue.type.TvTQueue;
import me.joeleoli.practice.manager.ManagerHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class QueueManager {

    @Getter private Map<UUID, IQueue> queues = new HashMap<>();

    public QueueManager() {
        initiateQueues();
    }

    private void initiateQueues() {
        for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
            SoloQueue s1 = new SoloQueue(ladder, true);
            SoloQueue s2 = new SoloQueue(ladder, false);
            TvTQueue t = new TvTQueue(ladder);

            this.queues.put(s1.getIdentifier(), s1);
            this.queues.put(s2.getIdentifier(), s2);
            this.queues.put(t.getIdentifier(), t);
        }
    }

}