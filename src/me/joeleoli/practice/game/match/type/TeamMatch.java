package me.joeleoli.practice.game.match.type;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.game.cache.CachedInventory;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.game.match.MatchStatus;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.game.queue.type.TvTQueue;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.util.GameUtils;

import mkremins.fanciful.FancyMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class TeamMatch implements IMatch {

    @Getter private UUID identifier;
    @Getter private TvTQueue queue;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private Map<UUID, Boolean> team1 = new HashMap<>();
    @Getter private Map<UUID, Boolean> team2 = new HashMap<>();
    @Getter private List<UUID> spectators = new ArrayList<>();
    @Getter private List<UUID> leftMatch = new ArrayList<>();
    @Getter private Map<UUID, CachedInventory> cachedInventories = new HashMap<>();

    public TeamMatch(TvTQueue queue, Ladder ladder, Arena arena, MatchType matchType, List<Player> team1, List<Player> team2) {
        this.identifier = UUID.randomUUID();
        this.queue = queue;
        this.ladder = ladder;
        this.arena = arena;
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = matchType;

        for (Player p : team1) {
            this.team1.put(p.getUniqueId(), true);

            p.setShowEntities(new ArrayList<>());
            p.addShowEntities(p.getUniqueId());

            for (Player p2 : team1) {
                p.addShowEntities(p2.getUniqueId());
            }

            for (Player p2 : team2) {
                p.addShowEntities(p2.getUniqueId());
            }

            GameUtils.resetPlayer(p);

        	PracticeProfile data = ManagerHandler.getPlayerManager().getPlayerProfile(p);

            if (data.getStatus() == PlayerStatus.EDITING_KITS) {
                ManagerHandler.getKitEditManager().getEditKits().remove(p.getUniqueId());
            }

        	data.setCurrentMatch(this);
        	data.setStatus(PlayerStatus.PLAYING);
        	data.showKits(ladder);

        	p.updateInventory();
        	p.spigot().setCollidesWithEntities(true);
            p.setCanPickupItems(true);
        	p.teleport(arena.getLocation1());
        	p.setMaximumNoDamageTicks(ladder.getHitDelay());

        	ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(team1);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(team2);
        }

        for (Player p : team2) {
            this.team2.put(p.getUniqueId(), true);

            p.setShowEntities(new ArrayList<>());
            p.addShowEntities(p.getUniqueId());

            for (Player p2 : team1) {
                p.addShowEntities(p2.getUniqueId());
            }

            for (Player p2 : team2) {
                p.addShowEntities(p2.getUniqueId());
            }

            GameUtils.resetPlayer(p);

            PracticeProfile data = ManagerHandler.getPlayerManager().getPlayerProfile(p);

            if (data.getStatus() == PlayerStatus.EDITING_KITS) {
                ManagerHandler.getKitEditManager().getEditKits().remove(p.getUniqueId());
            }

            data.setCurrentMatch(this);
            data.setStatus(PlayerStatus.PLAYING);
            data.showKits(ladder);

            p.updateInventory();
            p.spigot().setCollidesWithEntities(true);
            p.setCanPickupItems(true);
        	p.teleport(arena.getLocation2());
        	p.setMaximumNoDamageTicks(ladder.getHitDelay());

            ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(team1);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(team2);
        }

        for (Player p : getPlayers()) {
        	ManagerHandler.getEntityHider().hideAllPlayers(p);
        }

        for (Player p : getPlayers()) {
        	for (Player p2 : getPlayers()) {
        		ManagerHandler.getEntityHider().showEntity(p, p2);
        		ManagerHandler.getEntityHider().showEntity(p2, p);
        	}
        }

        new BukkitRunnable() {
            private int i = 5;

            public void run() {
                if (matchStatus == MatchStatus.FINISHED || matchStatus == MatchStatus.CANCELED) {
                    this.cancel();
                    return;
                }

                if (this.i <= 0) {
                    this.cancel();

                    startMatch();
                    playSound(Sound.NOTE_PIANO, 2.0f);

                    return;
                }

                if (this.i != 1) {
                    sendMessage(ChatColor.GRAY + "The match is starting in " + ChatColor.AQUA + this.i + ChatColor.GRAY + " seconds...");
                }
                else {
                    sendMessage(ChatColor.GRAY + "The match is starting in " + ChatColor.AQUA + this.i + ChatColor.GRAY + " second...");
                }

                playSound(Sound.NOTE_PIANO, 1.0f);
                --this.i;
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players.addAll(this.getTeam1Players());
        players.addAll(this.getTeam2Players());
    	return players;
    }

    @Override
    public List<Player> getTeam(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return this.getTeam1Players();
        }
        else {
            return this.getTeam2Players();
        }
    }

    @Override
    public List<Player> getOpponents(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return this.getTeam2Players();
        }
        else {
            return this.getTeam1Players();
        }
    }

    @Override
    public int getOpponentsLeft(Player player) {
        if (this.team1.containsKey(player.getUniqueId())) {
            return Collections.frequency(new ArrayList<>(this.team2.values()), true);
        }
        else {
            return Collections.frequency(new ArrayList<>(this.team1.values()), true);
        }
    }
    
    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (this.team1.containsKey(player.getUniqueId())) {
            if (!this.team1.get(player.getUniqueId())) {
                return;
            }
        }
        else {
            if (!this.team2.get(player.getUniqueId())) {
                return;
            }
        }
    	
    	if (deathMessage.contains("has left the match.")) {
    		this.leftMatch.add(player.getUniqueId());
    	}
    	else {
            playSound(Sound.AMBIENCE_THUNDER, 10.0f);
        }
    	
        this.cachedInventories.put(player.getUniqueId(), Cache.getCachedInventory(player, true));

        if (this.team1.containsKey(player.getUniqueId())) {
            this.team1.replace(player.getUniqueId(), false);
        	
        	Set<Boolean> values = new HashSet<>(this.team1.values());
        	boolean isUnique = values.size() == 1;
        	
        	if (isUnique) {
                this.endMatch(this.team2, this.team1);
        	}
        	else {
                for (Player p : this.getPlayers()) {
                    p.removeShowEntities(player.getUniqueId());
                }

        		this.startSpectating(player);

        		if (location != null) {
                    player.teleport(location.clone().add(0, 3, 0));
                }
        	}
        }
        else if (this.team2.containsKey(player.getUniqueId())) {
        	this.team2.replace(player.getUniqueId(), false);
        	
        	Set<Boolean> values = new HashSet<>(this.team2.values());
        	boolean isUnique = values.size() == 1;
        	
        	if (isUnique) {
        		this.endMatch(this.team1, this.team2);
        	}
        	else {
                for (Player p : this.getPlayers()) {
                    p.removeShowEntities(player.getUniqueId());
                }

        		this.startSpectating(player);

                if (location != null) {
                    player.teleport(location.clone().add(0, 3, 0));
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isOnline()) {
                            player.setHealth(20);
                            player.setFireTicks(0);
                        }
                    }
                }.runTaskLater(PracticePlugin.getInstance(), 10L);
        	}
        }
    }

    @Override
    public boolean isDead(Player player) {
        return this.team1.containsKey(player.getUniqueId()) ? !this.team1.get(player.getUniqueId()) : !this.team2.get(player.getUniqueId());
    }

    @Override
    public void sendMessage(String message) {
        for (Player p : getPlayers()) {
            p.sendMessage(message);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            }
            else {
                this.spectators.remove(uuid);
            }
        }
    }

    public void sendMessage(FancyMessage fancyMessage) {
        for (Player p : getPlayers()) {
            fancyMessage.send(p);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                fancyMessage.send(Bukkit.getPlayer(uuid));
            } else {
                this.spectators.remove(uuid);
            }
        }
    }

    private void playSound(Sound sound, float idk2) {
        for (Player p : getPlayers()) {
            p.playSound(p.getLocation(), sound, 10.0f, idk2);
        }

        for (UUID uuid : this.spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), sound, 10.0F, idk2);
            }
            else {
                this.spectators.remove(uuid);
            }
        }
    }

    private List<Player> getTeam1Players() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : this.team1.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            players.add(p);
        }

        return players;
    }

    private List<Player> getTeam2Players() {
        List<Player> players = new ArrayList<>();

        for (UUID uuid : this.team2.keySet()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            players.add(p);
        }

        return players;
    }

    @Override
    public void cancelMatch(String cancelReason) {
        this.sendMessage(ChatColor.DARK_RED + "The match has been canceled for: " + ChatColor.RED + cancelReason);

        if (queue != null) {
            queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        for (Player p : getPlayers()) {
        	if (leftMatch.contains(p.getUniqueId())) continue;
        	
        	GameUtils.resetPlayer(p);
            
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(p);
            profile.setStatus(PlayerStatus.LOBBY);
            profile.setCurrentMatch(null);
            
            if (profile.getParty() != null) {
            	if (profile.getParty().getLeader() == p) {
            		p.getInventory().setContents(GameUtils.getPartyLeaderInventory());
            	}
            	else {
            		p.getInventory().setContents(GameUtils.getPartyMemberInventory());
            	}
            }
            else {
            	p.getInventory().setContents(GameUtils.getLobbyInventory());
            }

            p.updateInventory();
            p.setAllowFlight(false);
            p.setMaximumNoDamageTicks(19);
            
            ManagerHandler.getConfig().teleportToSpawn(p);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(getPlayers());
        }
        
        cleanSpectators();
        
        ManagerHandler.getMatchManager().getMatches().remove(this.identifier);

        this.matchStatus = MatchStatus.CANCELED;
    }
    
    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;
        
        for (Player p : this.getTeam1Players()) {
        	p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 2, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
        }

        for (Player p : this.getTeam2Players()) {
        	p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 2, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
        }
    }
    
    private void endMatch(Map<UUID, Boolean> winners, Map<UUID, Boolean> losers) {
        FancyMessage winnerClickables = new FancyMessage("Winners").color(ChatColor.GREEN).then(": ").color(ChatColor.GRAY);
        FancyMessage loserClickables = new FancyMessage("Losers").color(ChatColor.RED).then(": ").color(ChatColor.GRAY);

        if (queue != null) {
            queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        int i = 0;

        for (Entry<UUID, Boolean> entry : winners.entrySet()) {
            i++;

            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                continue;
            }

            player.setShowEntities(new ArrayList<>());

            if (i == winners.size()) {
                winnerClickables.then(player.getName()).color(ChatColor.GREEN).command("/inventory " + player.getName());
            }
            else {
                winnerClickables.then(player.getName() + ", ").color(ChatColor.GREEN).command("/inventory " + player.getName());
            }

            if (entry.getValue()) {
                Cache.storeInventory(player, false);
            }

            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            profile.setUnrankedWins(profile.getUnrankedWins() + 1);
            profile.setStatus(PlayerStatus.LOBBY);
            profile.setCurrentMatch(null);
            profile.save(null);

            GameUtils.resetPlayer(player);

            if (profile.getParty() != null) {
                if (profile.getParty().getLeader() == player) {
                    player.getInventory().setContents(GameUtils.getPartyLeaderInventory());
                }
                else {
                    player.getInventory().setContents(GameUtils.getPartyMemberInventory());
                }
            }
            else {
                player.getInventory().setContents(GameUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            ManagerHandler.getEntityHider().showAllPlayers(player);
            ManagerHandler.getConfig().teleportToSpawn(player);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(getPlayers());
        }

        i = 0;

        for (Entry<UUID, Boolean> entry : losers.entrySet()) {
            i++;

            Player player = Bukkit.getPlayer(entry.getKey());

            if (player == null || !player.isOnline()) {
                continue;
            }

            player.setShowEntities(new ArrayList<>());

            if (i == losers.size()) {
                loserClickables.then(player.getName()).color(ChatColor.RED).command("/inventory " + player.getName());
            }
            else {
                loserClickables.then(player.getName() + ", ").color(ChatColor.RED).command("/inventory " + player.getName());
            }

            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            profile.setUnrankedLosses(profile.getUnrankedLosses() + 1);
            profile.setStatus(PlayerStatus.LOBBY);
            profile.setCurrentMatch(null);
            profile.save(null);

            GameUtils.resetPlayer(player);

            if (profile.getParty() != null) {
                if (profile.getParty().getLeader() == player) {
                    player.getInventory().setContents(GameUtils.getPartyLeaderInventory());
                }
                else {
                    player.getInventory().setContents(GameUtils.getPartyMemberInventory());
                }
            }
            else {
                player.getInventory().setContents(GameUtils.getLobbyInventory());
            }

            player.setMaximumNoDamageTicks(19);
            player.setAllowFlight(false);
            player.updateInventory();

            ManagerHandler.getEntityHider().showAllPlayers(player);
            ManagerHandler.getConfig().teleportToSpawn(player);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(getPlayers());
        }

        for (Entry<UUID, CachedInventory> entry : this.cachedInventories.entrySet()) {
            Cache.storeInventory(entry.getKey(), entry.getValue());
        }

        this.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");
        this.sendMessage(ChatColor.AQUA + "Match Information ");
        this.sendMessage(winnerClickables);
        this.sendMessage(loserClickables);
        this.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");

        ManagerHandler.getMatchManager().getMatches().remove(this.identifier);

        cleanSpectators();

        this.matchStatus = MatchStatus.FINISHED;
    }

    private void startSpectating(Player player) {
        GameUtils.resetPlayer(player);
        player.updateInventory();
        player.setMaximumNoDamageTicks(19);
        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player p : getPlayers()) {
            ManagerHandler.getEntityHider().hideEntity(p, player);
            ManagerHandler.getEntityHider().showEntity(player, p);
        }

        for (UUID uuid : this.spectators) {
            Player p = Bukkit.getPlayer(uuid);

            if (p != null && p.isOnline()) {
                ManagerHandler.getEntityHider().hideEntity(p, player);
                ManagerHandler.getEntityHider().hideEntity(player, p);
            }
        }
    }

    private void cleanSpectators() {
        Iterator<UUID> specIterator = spectators.iterator();

        while(specIterator.hasNext()) {
            Player p = Bukkit.getPlayer(specIterator.next());
            specIterator.remove();

            if (p != null) {
                ManagerHandler.getSpectateManager().stopSpectating(p, false);
                p.sendMessage(ChatColor.RED + "The match has finished.");
            }
        }
    }

}