package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.party.PartyListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    @Getter private Map<UUID, Party> parties;
    
    public PartyManager() {
        this.parties = new HashMap<>();
        new PartyListener();
    }
    
    public Party getParty(UUID identifier) {
        if (this.parties.containsKey(identifier)) {
            return this.parties.get(identifier);
        }

        return null;
    }
    
    public void addParty(Party party) {
        this.parties.put(party.getIdentifier(), party);
    }
    
    public void removeParty(Party party) {
        this.parties.remove(party.getIdentifier());
    }

    public int getPartyInvAmount() {
        if (parties.size() > 9) return 9;
        if (parties.size() > 18) return 18;
        if (parties.size() > 27) return 27;
        if (parties.size() > 36) return 36;
        if (parties.size() > 45) return 45;
        return 54;
    }
    
}