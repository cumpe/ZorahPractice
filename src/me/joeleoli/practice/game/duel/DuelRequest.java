package me.joeleoli.practice.game.duel;

import lombok.Getter;
import me.joeleoli.practice.game.ladder.Ladder;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DuelRequest {

    @Getter private UUID identifier;
    @Getter private UUID sender;
    @Getter private UUID receiver;
    @Getter private Ladder ladder;

    public DuelRequest(Player sender, Player receiver, Ladder ladder) {
        this.identifier = UUID.randomUUID();
        this.sender = sender.getUniqueId();
        this.receiver = receiver.getUniqueId();
        this.ladder = ladder;

        new FancyMessage(
                "You have been sent a ").color(ChatColor.GRAY)
                .then(ladder.getName()).color(ChatColor.AQUA)
                .then(" duel request by ").color(ChatColor.GRAY)
                .then(sender.getName()).color(ChatColor.AQUA)
                .then(".").color(ChatColor.GRAY)
                .then(" [Click to Accept]").color(ChatColor.GREEN).command("/duel accept " + sender.getName())
                .send(receiver);

        sender.sendMessage(ChatColor.GRAY + "You have sent " + ChatColor.AQUA + receiver.getName() + ChatColor.GRAY + " a duel request.");
    }

}