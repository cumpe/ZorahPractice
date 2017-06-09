package me.joeleoli.practice.player;

import lombok.Getter;
import lombok.Setter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.file.FileConfig;
import me.joeleoli.practice.data.runnable.GenericCallback;
import me.joeleoli.practice.game.duel.DuelRequest;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.QueueData;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.time.ManualTimer;
import me.joeleoli.practice.util.InventoryUtils;
import me.joeleoli.practice.util.ItemBuilder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PracticeProfile {

    private FileConfig file;
    @Getter private UUID identifier;
    @Getter @Setter private PlayerStatus status;

    @Getter @Setter private Party party;
    @Getter @Setter private IMatch currentMatch, spectatingMatch;
    @Getter @Setter private IQueue currentQueue;
    @Getter @Setter private QueueData queueData;

    @Getter private Map<Ladder, PlayerKits> ladderKits = new HashMap<>();
    @Getter private Map<Ladder, PlayerElo> ladderRatings = new HashMap<>();

    @Getter @Setter private int matchesPlayed, rankedWins, rankedLosses, unrankedWins, unrankedLosses = 0;

    @Getter @Setter private boolean hidingMessages, hidingChat, hidingDuels, hidingScoreboard;

    private Map<UUID, DuelRequest> duelRequests = new HashMap<>();

    @Getter private ManualTimer enderpearlTimer;

    public PracticeProfile(Player player) {
        this.file = new FileConfig(new File(PracticePlugin.getInstance().getDataFolder() + "/playerdata/"), player.getUniqueId().toString() + ".yml");
        this.identifier = player.getUniqueId();
        this.status = PlayerStatus.LOBBY;

        if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
            for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                this.ladderKits.put(ladder, new PlayerKits());
                this.ladderRatings.put(ladder, new PlayerElo(1000));
            }
        }

        this.enderpearlTimer = new ManualTimer(false);

        this.load();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.identifier);
    }

    public void setRating(Ladder ladder, Integer rating) {
        this.ladderRatings.put(ladder, new PlayerElo(rating));
    }

    public int getGlobalRating() {
        int units = 0;
        int sum = 0;

        for (Map.Entry<Ladder, PlayerElo> entry : ladderRatings.entrySet()) {
            units++;
            sum += entry.getValue().toInteger();
        }

        return sum / units;
    }

    public void showKits(Ladder ladder) {
        PlayerKits kits = ladderKits.get(ladder);

        this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GOLD + "Default Kit").getItem());

        if (kits.getKit1() != null) {
            this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.YELLOW + "Custom Kit #1").getItem());
        }

        if (kits.getKit2() != null) {
            this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.YELLOW + "Custom Kit #2").getItem());
        }

        if (kits.getKit3() != null) {
            this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.YELLOW + "Custom Kit #3").getItem());
        }

        if (kits.getKit4() != null) {
            this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.YELLOW + "Custom Kit #4").getItem());
        }

        if (kits.getKit5() != null) {
            this.getPlayer().getInventory().addItem(new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.YELLOW + "Custom Kit #5", new String[0]).getItem());
        }
    }

    public void addRequest(Player player, DuelRequest request) {
        this.duelRequests.put(player.getUniqueId(), request);
    }

    public void removeRequest(Player player) {
        this.duelRequests.remove(player.getUniqueId());
    }

    public boolean hasRequest(Player player) {
        return this.duelRequests.containsKey(player.getUniqueId());
    }

    public DuelRequest getRequest(Player player) {
        return this.duelRequests.get(player.getUniqueId());
    }

    private void load() {
        ManagerHandler.getStorageBackend().createProfile(this.getPlayer());

        this.matchesPlayed = this.file.getConfig().getInt("matches-played");
        this.rankedWins = this.file.getConfig().getInt("ranked-wins");
        this.rankedLosses = this.file.getConfig().getInt("ranked-losses");
        this.unrankedWins = this.file.getConfig().getInt("unranked-wins");
        this.unrankedLosses = this.file.getConfig().getInt("unranked-losses");

        this.hidingMessages = this.file.getConfig().getBoolean("hiding-messages");
        this.hidingChat = this.file.getConfig().getBoolean("hiding-chat");
        this.hidingDuels = this.file.getConfig().getBoolean("hiding-duels");
        this.hidingScoreboard = this.file.getConfig().getBoolean("hiding-scoreboard");

        for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
            PlayerElo soloRating = new PlayerElo((file.getConfig().contains("ratings." + ladder.getName()) ? file.getConfig().getInt("ratings." + ladder.getName()) : 1000));

            if (!this.ladderRatings.containsKey(ladder)) {
                this.ladderRatings.put(ladder, soloRating);
            }
            else {
                this.ladderRatings.replace(ladder, soloRating);
            }

            if (this.file.getConfig().contains("kits." + ladder.getName())) {
                if (this.file.getConfig().get("kits." + ladder.getName() + ".1") != null) {
                    ladderKits.get(ladder).setKit1(InventoryUtils.playerInventoryFromString(file.getConfig().getString("kits." + ladder.getName() + ".1")));
                }

                if (this.file.getConfig().get("kits." + ladder.getName() + ".2") != null) {
                    ladderKits.get(ladder).setKit2(InventoryUtils.playerInventoryFromString(file.getConfig().getString("kits." + ladder.getName() + ".2")));
                }

                if (this.file.getConfig().get("kits." + ladder.getName() + ".3") != null) {
                    ladderKits.get(ladder).setKit3(InventoryUtils.playerInventoryFromString(file.getConfig().getString("kits." + ladder.getName() + ".3")));

                }
                if (this.file.getConfig().get("kits." + ladder.getName() + ".4") != null) {
                    ladderKits.get(ladder).setKit4(InventoryUtils.playerInventoryFromString(file.getConfig().getString("kits." + ladder.getName() + ".4")));
                }

                if (this.file.getConfig().get("kits." + ladder.getName() + ".5") != null) {
                    ladderKits.get(ladder).setKit5(InventoryUtils.playerInventoryFromString(file.getConfig().getString("kits." + ladder.getName() + ".5")));
                }
            }
            else {
                this.ladderKits.put(ladder, new PlayerKits());
            }
        }
    }

    public void save(GenericCallback callback) {
        ManagerHandler.getStorageBackend().saveProfile(this.getPlayer(), callback);

        this.file.getConfig().set("ranked-wins", this.rankedWins);
        this.file.getConfig().set("ranked-losses", this.rankedLosses);
        this.file.getConfig().set("unranked-wins", this.unrankedWins);
        this.file.getConfig().set("unranked-losses", this.unrankedLosses);
        this.file.getConfig().set("matches-played", this.matchesPlayed);

        this.file.getConfig().set("hiding-messages", this.hidingMessages);
        this.file.getConfig().set("hiding-chat", this.hidingChat);
        this.file.getConfig().set("hiding-duels", this.hidingDuels);
        this.file.getConfig().set("hiding-scoreboard", this.hidingScoreboard);

        for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
            if (!this.ladderRatings.containsKey(ladder)) {
                this.ladderRatings.put(ladder, new PlayerElo(1000));
            }

            if (!this.ladderKits.containsKey(ladder)) {
                this.ladderKits.put(ladder, new PlayerKits());
            }

            this.file.getConfig().set("ratings." + ladder.getName(), this.ladderRatings.get(ladder).toInteger());

            PlayerKits playerKits = this.ladderKits.get(ladder);

            if (playerKits.getKit1() != null) {
                this.file.getConfig().set("kits." + ladder.getName() + ".1", InventoryUtils.playerInvToString(playerKits.getKit1()));
            }

            if (playerKits.getKit2() != null) {
                this.file.getConfig().set("kits." + ladder.getName() + ".2", InventoryUtils.playerInvToString(playerKits.getKit2()));
            }

            if (playerKits.getKit3() != null) {
                this.file.getConfig().set("kits." + ladder.getName() + ".3", InventoryUtils.playerInvToString(playerKits.getKit3()));
            }

            if (playerKits.getKit4() != null) {
                this.file.getConfig().set("kits." + ladder.getName() + ".4", InventoryUtils.playerInvToString(playerKits.getKit4()));
            }

            if (playerKits.getKit5() != null) {
                this.file.getConfig().set("kits." + ladder.getName() + ".5", InventoryUtils.playerInvToString(playerKits.getKit5()));
            }
        }

        this.file.save();
    }

}