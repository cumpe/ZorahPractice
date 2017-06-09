package me.joeleoli.practice.game.match.type;

import lombok.Getter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.game.cache.CachedMatch;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.IMatch;
import me.joeleoli.practice.game.match.MatchStatus;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.game.queue.type.SoloQueue;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerElo;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.PlayerBoard;
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

public class SoloMatch implements IMatch {

    @Getter private SoloQueue queue;
    @Getter private UUID identifier;
    @Getter private Ladder ladder;
    @Getter private Arena arena;
    @Getter private Boolean isRanked;
    @Getter private MatchStatus matchStatus;
    @Getter private MatchType matchType;
    @Getter private Timestamp startTimestamp;
    @Getter private Long startNano;
    @Getter private List<UUID> spectators;
    @Getter private Player player1;
    @Getter private Player player2;
    
    public SoloMatch(SoloQueue queue, Ladder ladder, Arena arena, Boolean isRanked, Player player1, Player player2) {
        this.queue = queue;
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.arena = arena;
        this.isRanked = isRanked;
        this.spectators = new ArrayList<>();
        this.matchStatus = MatchStatus.STARTING;
        this.matchType = MatchType.ONE_VERSUS_ONE;
        this.player1 = player1;
        this.player2 = player2;

        ManagerHandler.getEntityHider().hideAllPlayers(player1);
        ManagerHandler.getEntityHider().hideAllPlayers(player2);

        player1.setMaximumNoDamageTicks(ladder.getHitDelay());
        player2.setMaximumNoDamageTicks(ladder.getHitDelay());

        player1.teleport(arena.getLocation1());
        player2.teleport(arena.getLocation2());

        PracticeProfile profile1 = ManagerHandler.getPlayerManager().getPlayerProfile(player1);
        PracticeProfile profile2 = ManagerHandler.getPlayerManager().getPlayerProfile(player2);

        if (profile1.getStatus() == PlayerStatus.EDITING_KITS) {
            ManagerHandler.getKitEditManager().getEditKits().remove(player1.getUniqueId());
        }

        if (profile2.getStatus() == PlayerStatus.EDITING_KITS) {
            ManagerHandler.getKitEditManager().getEditKits().remove(player2.getUniqueId());
        }

        profile1.setCurrentMatch(this);
        profile2.setCurrentMatch(this);

        profile1.setStatus(PlayerStatus.PLAYING);
        profile2.setStatus(PlayerStatus.PLAYING);

        GameUtils.resetPlayer(player1);
        GameUtils.resetPlayer(player2);

        profile1.showKits(ladder);
        profile2.showKits(ladder);

        player1.updateInventory();
        player2.updateInventory();

        player1.spigot().setCollidesWithEntities(true);
        player2.spigot().setCollidesWithEntities(true);

        player1.setCanPickupItems(true);
        player2.setCanPickupItems(true);

        if (isRanked) {
            boolean isMore = false;
            int difference = profile1.getLadderRatings().get(ladder).toInteger() - profile2.getLadderRatings().get(ladder).toInteger();

            if (difference < 0) {
                isMore = true;
                difference = Math.abs(difference);
            }

            player1.sendMessage(ChatColor.AQUA + "Your Opponent: " + ChatColor.GREEN + player2.getName() + ChatColor.GRAY + " +" + (isMore ? ChatColor.GREEN : ChatColor.RED) + difference + ChatColor.GRAY + " (" + (isMore ? ChatColor.GREEN : ChatColor.RED) + profile2.getLadderRatings().get(ladder).toInteger() + ChatColor.GRAY + ")");
            player2.sendMessage(ChatColor.AQUA + "Your Opponent: " + ChatColor.GREEN + player1.getName() + ChatColor.GRAY + " +" + (!isMore ? ChatColor.GREEN : ChatColor.RED) + difference + ChatColor.GRAY + " (" + (!isMore ? ChatColor.GREEN : ChatColor.RED) + profile1.getLadderRatings().get(ladder).toInteger() + ChatColor.GRAY + ")");
        } else {
            player1.sendMessage(ChatColor.AQUA + "Your Opponent: " + ChatColor.GREEN + player2.getName());
            player2.sendMessage(ChatColor.AQUA + "Your Opponent: " + ChatColor.GREEN + player1.getName());
        }

        player1.setShowEntities(Arrays.asList(player1.getUniqueId(), player2.getUniqueId()));
        player2.setShowEntities(Arrays.asList(player1.getUniqueId(), player2.getUniqueId()));

        ManagerHandler.getEntityHider().showEntity(player1, player2);
        ManagerHandler.getEntityHider().showEntity(player2, player1);

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
                } else {
                    sendMessage(ChatColor.GRAY + "The match is starting in " + ChatColor.AQUA + this.i + ChatColor.GRAY + " second...");
                }

                playSound(Sound.NOTE_PIANO, 1.0f);
                --this.i;
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 20L);
    }

    @Override
    public List<Player> getPlayers() {
        return Arrays.asList(player1, player2);
    }

    @Override
    public List<Player> getTeam(Player player) {
        return Collections.emptyList();
    }

    @Override
    public List<Player> getOpponents(Player player) {
        if (player.equals(this.player1)) {
            return Collections.singletonList(player2);
        }

        if (player.equals(this.player2)) {
            return Collections.singletonList(player1);
        }

        return Collections.emptyList();
    }

    @Override
    public int getOpponentsLeft(Player player) {
        return 1;
    }
    
    @Override
    public void handleDeath(Player player, Location location, String deathMessage) {
        if (location != null) {
            player.teleport(location.clone().add(0, 3, 0));
        }

        this.startSpectating(player);

    	this.sendMessage(deathMessage);

    	if (!deathMessage.contains("left the match")) {
    	    playSound(Sound.AMBIENCE_THUNDER, 10.0f);
    	    ManagerHandler.getEntityHider().hideEntity(this.player1, this.player2);
            ManagerHandler.getEntityHider().hideEntity(this.player2, this.player1);
        }

        Cache.storeInventory(player, true);

        if (player == this.player1) {
            this.endMatch(this.player2, this.player1);
        }

        if (player == this.player2) {
            this.endMatch(this.player1, this.player2);
        }
    }

    @Override
    public boolean isDead(Player player) {
        return false;
    }

    @Override
    public void sendMessage(String message) {
        for (Player p : getPlayers()) {
            p.sendMessage(message);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            } else {
                spectators.remove(uuid);
            }
        }
    }

    public void sendMessage(FancyMessage fancyMessage) {
        for (Player p : getPlayers()) {
            fancyMessage.send(p);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                fancyMessage.send(Bukkit.getPlayer(uuid));
            } else {
                spectators.remove(uuid);
            }
        }
    }

    private void playSound(Sound sound, float idk2) {
        for (Player p : getPlayers()) {
            p.playSound(p.getLocation(), sound, 10.0f, idk2);
        }

        for (UUID uuid : spectators) {
            if (Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).playSound(Bukkit.getPlayer(uuid).getLocation(), sound, 10.0F, idk2);
            }
            else {
                spectators.remove(uuid);
            }
        }
    }

    @Override
    public void cancelMatch(String cancelReason) {
        this.matchStatus = MatchStatus.CANCELED;

        this.sendMessage(ChatColor.DARK_RED + "The match has been canceled for: " + ChatColor.RED + cancelReason);

        this.player1.setShowEntities(new ArrayList<>());
        this.player2.setShowEntities(new ArrayList<>());

        PracticeProfile firstProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player1);
        PracticeProfile secondProfile = ManagerHandler.getPlayerManager().getPlayerProfile(player2);

        firstProfile.setStatus(PlayerStatus.LOBBY);
        secondProfile.setStatus(PlayerStatus.LOBBY);

        firstProfile.setCurrentMatch(null);
        secondProfile.setCurrentMatch(null);

        if (this.queue != null) {
            this.queue.setPlayingAmount(queue.getPlayingAmount() - 2);
        }

        GameUtils.resetPlayer(this.player1);
        GameUtils.resetPlayer(this.player2);

        this.player1.setMaximumNoDamageTicks(19);
        this.player2.setMaximumNoDamageTicks(19);

        this.player1.getInventory().setContents(GameUtils.getLobbyInventory());
        this.player2.getInventory().setContents(GameUtils.getLobbyInventory());

        this.player1.updateInventory();
        this.player2.updateInventory();

        ManagerHandler.getScoreboardHandler().getPlayerBoard(this.player1.getUniqueId()).addUpdate(this.player2);
        ManagerHandler.getScoreboardHandler().getPlayerBoard(this.player2.getUniqueId()).addUpdate(this.player1);

        ManagerHandler.getEntityHider().showAllPlayers(this.player1);
        ManagerHandler.getEntityHider().showAllPlayers(this.player2);

        ManagerHandler.getConfig().teleportToSpawn(this.player1);
        ManagerHandler.getConfig().teleportToSpawn(this.player2);
        
        ManagerHandler.getMatchManager().getMatches().remove(this.identifier);
        
        cleanSpectators();
    }
    
    private void startMatch() {
        this.startTimestamp = new Timestamp(System.currentTimeMillis());
        this.startNano = System.nanoTime();
        this.matchStatus = MatchStatus.ONGOING;

        this.player1.teleport(new Location(this.player1.getWorld(), this.player1.getLocation().getX(), this.player1.getLocation().getY() + 2, this.player1.getLocation().getZ(), this.player1.getLocation().getYaw(), this.player1.getLocation().getPitch()));
        this.player2.teleport(new Location(this.player2.getWorld(), this.player2.getLocation().getX(), this.player2.getLocation().getY() + 2, this.player2.getLocation().getZ(), this.player2.getLocation().getYaw(), this.player2.getLocation().getPitch()));
    }
    
    private void endMatch(Player winner, Player loser) {
        this.matchStatus = MatchStatus.FINISHED;

        if (this.queue != null) {
            this.queue.setPlayingAmount(this.queue.getPlayingAmount() - 2);
        }

        winner.setMaximumNoDamageTicks(19);
        loser.setMaximumNoDamageTicks(19);

        PracticeProfile winnerProfile = ManagerHandler.getPlayerManager().getPlayerProfile(winner);
        PracticeProfile loserProfile = ManagerHandler.getPlayerManager().getPlayerProfile(loser);

        winnerProfile.setStatus(PlayerStatus.LOBBY);
        loserProfile.setStatus(PlayerStatus.LOBBY);

        winnerProfile.setCurrentMatch(null);
        loserProfile.setCurrentMatch(null);

        winnerProfile.setMatchesPlayed(winnerProfile.getMatchesPlayed() + 1);
        loserProfile.setMatchesPlayed(loserProfile.getMatchesPlayed() + 1);

        int eloChange = 0;

        if (this.isRanked) {
            winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
            loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);

            PlayerElo winnerElo = winnerProfile.getLadderRatings().get(ladder);
            PlayerElo loserElo = loserProfile.getLadderRatings().get(ladder);

            int winnerNewElo= winnerElo.getNewRating(loserElo.toInteger(), 1);
            int loserNewElo = loserElo.getNewRating(winnerElo.toInteger(), 0);

            eloChange = (winnerNewElo - winnerElo.toInteger());

            winnerProfile.getLadderRatings().put(this.ladder, winnerElo.setRating(winnerNewElo));
            loserProfile.getLadderRatings().put(this.ladder, loserElo.setRating(loserNewElo));
        }
        else {
            winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
            loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
        }

        FancyMessage playerClickable;

        if (isRanked) {
            playerClickable = new FancyMessage("Winner").color(ChatColor.GREEN)
                    .then(": ").color(ChatColor.GRAY)
                    .then(winner.getName()).color(ChatColor.GRAY).command("/inventory " + winner.getName())
                    .then(" (").color(ChatColor.GRAY)
                    .then("+" + eloChange).color(ChatColor.GREEN)
                    .then(")").color(ChatColor.GRAY)
                    .then(" - ").color(ChatColor.GRAY)
                    .then("Loser").color(ChatColor.RED)
                    .then(": ").color(ChatColor.GRAY)
                    .then(loser.getName()).color(ChatColor.GRAY).command("/inventory " + loser.getName())
                    .then(" (").color(ChatColor.GRAY)
                    .then("-" + eloChange).color(ChatColor.RED)
                    .then(")").color(ChatColor.GRAY);
        }
        else {
            playerClickable = new FancyMessage("Winner").color(ChatColor.GREEN)
                    .then(": ").color(ChatColor.GRAY)
                    .then(winner.getName()).color(ChatColor.GRAY).command("/inventory " + winner.getName())
                    .then(" - ").color(ChatColor.GRAY)
                    .then("Loser").color(ChatColor.RED)
                    .then(": ").color(ChatColor.GRAY)
                    .then(loser.getName()).color(ChatColor.GRAY).command("/inventory " + loser.getName());
        }

        Cache.storeInventory(winner, false);

        winnerProfile.save(null);
        loserProfile.save(null);

        GameUtils.resetPlayer(winner);
        GameUtils.resetPlayer(loser);

        ManagerHandler.getEntityHider().showEntity(winner, loser);
        ManagerHandler.getEntityHider().showEntity(loser, winner);

        for (UUID uuid : this.getSpectators()) {
            Player p = Bukkit.getPlayer(uuid);

            if (p == null || !p.isOnline()) {
                continue;
            }

            for (UUID uuid2 : this.getSpectators()) {
                Player p2 = Bukkit.getPlayer(uuid2);

                if (p2 == null || !p2.isOnline()) {
                    continue;
                }

                ManagerHandler.getEntityHider().showEntity(p, p2);
                ManagerHandler.getEntityHider().showEntity(p2, p);
            }

            ManagerHandler.getEntityHider().showEntity(winner, p);
            ManagerHandler.getEntityHider().showEntity(p, winner);
            ManagerHandler.getEntityHider().showEntity(loser, p);
            ManagerHandler.getEntityHider().showEntity(p, loser);
        }

        this.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");
        this.sendMessage(ChatColor.AQUA + "Match Information");
        this.sendMessage(playerClickable);
        this.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "--------------------------------------");

        cleanSpectators();

        ManagerHandler.getMatchManager().getMatches().remove(this.identifier);

        CachedMatch cachedMatch = new CachedMatch(this.identifier, this.ladder.getName(), this.isRanked, this.arena.getName(), this.matchType, winner.getName(), winner.getUniqueId(), loser.getName(), loser.getUniqueId(), eloChange, Cache.inventories.get(winner.getUniqueId()), Cache.inventories.get(loser.getUniqueId()), startTimestamp, new Timestamp(System.currentTimeMillis()), 0);
        ManagerHandler.getStorageBackend().saveMatch(cachedMatch);

        new BukkitRunnable() {
            public void run() {
                if(winner.isOnline()) {
                    PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(winner);

                    if (profile != null && profile.getStatus() == PlayerStatus.LOBBY) {
                        winner.getInventory().setContents(GameUtils.getLobbyInventory());
                        winner.updateInventory();
                        ManagerHandler.getConfig().teleportToSpawn(winner);

                        PlayerBoard winnerBoard = ManagerHandler.getScoreboardHandler().getPlayerBoard(winner.getUniqueId());

                        if (winnerBoard != null) {
                            winnerBoard.addUpdate(loser);
                        }
                    }

                    winner.setFlying(false);
                    winner.setAllowFlight(false);
                }

                if(loser.isOnline()) {
                    PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(loser);

                    if (profile != null && profile.getStatus() == PlayerStatus.LOBBY) {
                        loser.getInventory().setContents(GameUtils.getLobbyInventory());
                        loser.updateInventory();
                        ManagerHandler.getConfig().teleportToSpawn(loser);

                        PlayerBoard loserBoard = ManagerHandler.getScoreboardHandler().getPlayerBoard(loser.getUniqueId());

                        if (loserBoard != null) {
                            loserBoard.addUpdate(winner);
                        }
                    }

                    loser.setFlying(false);
                    loser.setAllowFlight(false);
                }
            }
        }.runTaskLater(PracticePlugin.getInstance(), 20L * 5);
    }

    private void startSpectating(Player player) {
        player.setAllowFlight(true);

        new BukkitRunnable() {
            public void run() {
                player.setFlying(true);
            }
        }.runTaskLater(PracticePlugin.getInstance(), 10L);
    }

    private void cleanSpectators() {
        Iterator<UUID> specIterator = spectators.iterator();

        while(specIterator.hasNext()) {
            Player p = Bukkit.getPlayer(specIterator.next());

            if (p != null) {
                ManagerHandler.getSpectateManager().stopSpectating(p, false);
                p.sendMessage(ChatColor.RED + "The match has finished.");
            }

            specIterator.remove();
        }
    }

}