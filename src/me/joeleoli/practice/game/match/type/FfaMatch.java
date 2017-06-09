package me.joeleoli.practice.game.match.type;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.game.match.MatchStatus;
import me.joeleoli.practice.game.match.MatchType;
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

public class FfaMatch implements IMatch {

    @Getter private UUID identifier;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private Map<UUID, Boolean> alive = new HashMap<>();
    @Getter private List<UUID> spectators = new ArrayList<>();
    @Getter private List<UUID> leftMatch = new ArrayList<>();

    public FfaMatch(Ladder ladder, Arena arena, List<Player> players) {
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.arena = arena;
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = MatchType.PARTY_VERSUS_PARTY;

        int i = 0;

        for (Player p : players) {
            p.setShowEntities(new ArrayList<>());

            for (Player p2 : players) {
                p.addShowEntities(p2.getUniqueId());
            }

            this.alive.put(p.getUniqueId(), true);

            GameUtils.resetPlayer(p);

        	PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(p);

            if (profile.getStatus() == PlayerStatus.EDITING_KITS) {
                ManagerHandler.getKitEditManager().getEditKits().remove(p.getUniqueId());
            }

        	profile.setCurrentMatch(this);
        	profile.setStatus(PlayerStatus.PLAYING);
        	profile.showKits(ladder);

        	p.updateInventory();
        	p.spigot().setCollidesWithEntities(true);
        	p.setCanPickupItems(true);
        	p.setMaximumNoDamageTicks(ladder.getHitDelay());

            if (i == 0) {
                p.teleport(arena.getLocation1());
                i++;
            }
            else if (i == 1) {
                p.teleport(arena.getLocation2());
                i--;
            }
            else {
                p.teleport(arena.getLocation1());
                i = 0;
            }

        	ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(players);
            ManagerHandler.getScoreboardHandler().getPlayerBoard(p.getUniqueId()).addUpdates(players);
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

        if(!this.alive.isEmpty()) {
            Iterator<UUID> uuidIterator = this.alive.keySet().iterator();

            while(uuidIterator.hasNext()) {
                Player p = Bukkit.getPlayer(uuidIterator.next());

                if(p == null) {
                    uuidIterator.remove();
                }
                else {
                    players.add(p);
                }
            }
        }

        return players;
    }

    @Override
    public List<Player> getTeam(Player player) {
        return Collections.emptyList();
    }

    @Override
    public List<Player> getOpponents(Player player) {
        List<Player> enemies = new ArrayList<>();

        for (Player p : getPlayers()) {
            if (player != p) enemies.add(p);
        }

        return enemies;
    }

    @Override
    public int getOpponentsLeft(Player player) {
        return alive.size() - 1;
    }

    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (this.isDead(player)) {
            return;
        }

        this.alive.replace(player.getUniqueId(), false);

    	this.sendMessage(deathMessage);

    	if (deathMessage.contains("has left the match.")) {
    		this.leftMatch.add(player.getUniqueId());
    	} else {
            playSound(Sound.AMBIENCE_THUNDER, 10.0f);
        }

        Cache.storeInventory(player, true);

    	int alive = 0;

    	for (Boolean bool : this.alive.values()) {
    	    if (bool) {
    	        alive++;
            }
        }

        if (alive == 1) {
            this.endMatch(getLastAlive());
        }
        else {
    	    for (Player p : this.getPlayers()) {
                p.removeShowEntities(player.getUniqueId());
            }

            player.setMaximumNoDamageTicks(19);

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

    @Override
    public boolean isDead(Player player) {
        return !this.alive.get(player.getUniqueId());
    }

    private Player getLastAlive() {
        for (Entry<UUID, Boolean> entry : this.alive.entrySet()) {
            if (entry.getValue()) return Bukkit.getPlayer(entry.getKey());
        }

        return null;
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
            }
            else {
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

    @Override
    public void cancelMatch(String cancelReason) {
        this.sendMessage(ChatColor.DARK_RED + "The match has been canceled for: " + ChatColor.RED + cancelReason);

        for (Player player : getPlayers()) {
        	if (this.leftMatch.contains(player.getUniqueId())) continue;

        	GameUtils.resetPlayer(player);

            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            profile.setStatus(PlayerStatus.LOBBY);
            profile.setCurrentMatch(null);

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

        cleanSpectators();

        ManagerHandler.getMatchManager().getMatches().remove(this.identifier);

        this.matchStatus = MatchStatus.CANCELED;
    }

    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;

        for (Player p : getPlayers()) {
        	p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY() + 2, p.getLocation().getZ(), p.getLocation().getYaw(), p.getLocation().getPitch()));
        }
    }

    private void endMatch(Player winner) {
        Cache.storeInventory(winner, false);

        PracticeProfile winnerProfile = ManagerHandler.getPlayerManager().getPlayerProfile(winner);

        winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
        winnerProfile.save(null);

        FancyMessage winnerClickables = new FancyMessage("Winner").color(ChatColor.GREEN).then(": ").color(ChatColor.GRAY);
        FancyMessage loserClickables = new FancyMessage("Losers").color(ChatColor.RED).then(": ").color(ChatColor.GRAY);

        int alive = 0;
        int i = 0;

        for (Entry<UUID, Boolean> entry : this.alive.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());

            if(player == null) {
                this.alive.remove(entry.getKey());
                continue;
            }

            player.setShowEntities(new ArrayList<>());

            if (entry.getValue()) {
                alive++;
                Cache.storeInventory(player, false);
            } else {
                i++;

                player.getInventory().setContents(GameUtils.getLobbyInventory());
                player.updateInventory();
                ManagerHandler.getConfig().teleportToSpawn(player);
                ManagerHandler.getEntityHider().showAllPlayers(player);
                loserClickables.then(player.getName() + (i + 1 == getPlayers().size() - alive ? "" : ", ")).color(ChatColor.RED).command("/inventory " + player.getName());
            }

            PracticeProfile data = ManagerHandler.getPlayerManager().getPlayerProfile(player);

            if (player == winner) {
                data.setUnrankedWins(data.getUnrankedWins() + 1);
            }
            else {
                data.setUnrankedLosses(data.getUnrankedLosses() + 1);
            }

            data.setStatus(PlayerStatus.LOBBY);
            data.setCurrentMatch(null);
            data.save(null);

            GameUtils.resetPlayer(player);

            if (data.getParty() != null) {
                if (data.getParty().getLeader() == player) {
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