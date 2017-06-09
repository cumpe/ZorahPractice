package me.joeleoli.practice.game.cache;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Getter;

import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.util.InventoryUtils;

import java.sql.Timestamp;
import java.util.UUID;

public class CachedMatch {

    @Getter private UUID identifier;
    @Getter private boolean ranked;
    @Getter private int eloChange;
    @Getter private String ladder, arena, winner, loser;
    @Getter private UUID winnerUuid, loserUuid;
    @Getter private CachedInventory winnerInv, loserInv;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp, finishTimestamp;
    @Getter private int duration;
    
    public CachedMatch(UUID identifier, String ladder, boolean ranked, String arena, MatchType matchType, String winner, UUID winnerUUID, String loser, UUID loserUUID, int eloChange, CachedInventory winnerInv, CachedInventory loserInv, Timestamp startTimestamp, Timestamp finishTimestamp, int duration) {
        this.identifier = identifier;
        this.ranked = ranked;
        this.eloChange = eloChange;
        this.ladder = ladder;
        this.arena = arena;
        this.winner = winner;
        this.loser = loser;
        this.winnerUuid = winnerUUID;
        this.loserUuid = loserUUID;
        this.winnerInv = winnerInv;
        this.loserInv = loserInv;
        this.matchType = matchType;
        this.startTimestamp = startTimestamp;
        this.finishTimestamp = finishTimestamp;
        this.duration = duration;
    }
    
    public JsonObject getJsonData() {
        JsonParser parser = new JsonParser();
        JsonObject matchObject = new JsonObject();
        JsonObject winnerObject = new JsonObject();

        JsonElement winner_uuid = parser.parse(this.winnerUuid.toString());
        JsonElement winner_name = parser.parse(this.winner);
        JsonElement winner_inventory = parser.parse(InventoryUtils.inventoryToString(this.winnerInv.getInventory()));

        winnerObject.add("player_uuid", winner_uuid);
        winnerObject.add("player_name", winner_name);
        winnerObject.add("player_inventory", winner_inventory);

        JsonObject loserObject = new JsonObject();
        JsonElement loser_uuid = parser.parse(this.loserUuid.toString());
        JsonElement loser_name = parser.parse(this.loser);
        JsonElement loser_inventory = parser.parse(InventoryUtils.inventoryToString(this.loserInv.getInventory()));

        loserObject.add("player_uuid", loser_uuid);
        loserObject.add("player_name", loser_name);
        loserObject.add("player_inventory", loser_inventory);

        JsonElement ladder = parser.parse(this.ladder);
        JsonElement match_identifier = parser.parse(this.identifier.toString());
        JsonElement match_type = parser.parse("one_versus_one");
        JsonElement duration = parser.parse("" + this.duration);
        JsonElement startTime = parser.parse("" + this.startTimestamp.toString());
        JsonElement endTime = parser.parse("" + this.finishTimestamp.toString());

        matchObject.add("ladder", ladder);
        matchObject.add("match_identifier", match_identifier);
        matchObject.add("match_type", match_type);
        matchObject.add("duration", duration);
        matchObject.add("start_timestamp", startTime);
        matchObject.add("end_timestamp", endTime);
        matchObject.add("winner", winnerObject);
        matchObject.add("loser", loserObject);
        
        return matchObject;
    }

}