package me.joeleoli.practice.game.queue.type;

import lombok.Getter;
import lombok.Setter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.game.match.type.TeamMatch;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.QueueData;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.GameUtils;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TvTQueue implements IQueue {

    @Getter private UUID identifier;
    @Getter private Ladder ladder;

    @Getter @Setter private int playingAmount = 0;

    @Getter private LinkedList<QueueData> searchList;
    @Getter private BukkitTask queueTask;

    public TvTQueue(Ladder ladder) {
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.searchList = new LinkedList<>();
        this.startTask();
    }

    @Override
    public String getName() {
        return ladder.getName() + " 2v2";
    }

    @Override
    public boolean isRanked() {
        return false;
    }

    @Override
    public int getQueueingAmount() {
        return this.searchList.size();
    }

    @Override
    public void addToQueue(Object object) {
        Party party = (Party) object;

        List<Player> players = party.getPlayers();

        if (players.size() != 2) {
            party.sendMessage(ChatColor.RED + "Tried to put this party in 2v2 queue, but the party size doesn't equal 2 players.");
            return;
        }

        QueueData data = new QueueData(party.getIdentifier(), this, 0);

        for (Player p : players) {
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(p);

            profile.setCurrentQueue(this);
            profile.setQueueData(data);
            profile.setStatus(PlayerStatus.QUEUEING);

            if (p.getUniqueId().equals(party.getLeaderUuid())) {
                GameUtils.resetPlayer(p);
                p.getInventory().setContents(GameUtils.getQueueInventory());
                p.updateInventory();
            }

            if (p.getGameMode() == GameMode.CREATIVE) {
                p.setGameMode(GameMode.SURVIVAL);
            }
        }

        this.searchList.offer(data);

        party.sendMessage(ChatColor.GRAY + "You have been added to the " + ChatColor.AQUA + this.ladder.getName() + " 1v1 " + ChatColor.GRAY + "queue, please wait while we find you an opponent.");
    }

    @Override
    public void removeFromQueue(Object object) {
        this.removeFromQueue(object, true, true, true);
    }

    private void removeFromQueue(Object object, boolean send, boolean clean, boolean teleport) {
        this.searchList.remove(object);

        QueueData participant = (QueueData) object;
        Party party = ManagerHandler.getPartyManager().getParty((UUID) participant.getObject());

        List<Player> players = party.getPlayers();

        for (Player p : players) {
            PracticeProfile prof = ManagerHandler.getPlayerManager().getPlayerProfile(p);
            prof.setCurrentQueue(null);
            prof.setQueueData(null);
            prof.setStatus(PlayerStatus.LOBBY);

            if (clean) {
                GameUtils.resetPlayer(p);
                p.getPlayer().getInventory().setContents(GameUtils.getLobbyInventory());
                p.getPlayer().updateInventory();
            }

            if (teleport) {
                ManagerHandler.getConfig().teleportToSpawn(p);
            }

            if (send) {
                p.getPlayer().sendMessage(ChatColor.GRAY + "You have been removed from the queue.");
            }
        }
    }

    private void createMatch(QueueData data1, QueueData data2) {
        Arena arena = ManagerHandler.getArenaManager().getRandomArena();

        this.removeFromQueue(data1, false, true, false);
        this.removeFromQueue(data2, false, true, false);

        Party party1 = ManagerHandler.getPartyManager().getParty((UUID) data1.getObject());
        Party party2 = ManagerHandler.getPartyManager().getParty((UUID) data2.getObject());

        if (arena == null) {
            party1.sendMessage(ChatColor.GRAY + "There are no available arenas, you have been removed from the queue.");
            party2.sendMessage(ChatColor.GRAY + "There are no available arenas, you have been removed from the queue.");

            List<Player> players = new ArrayList<>();
            players.addAll(party1.getPlayers());
            players.addAll(party2.getPlayers());

            for (Player p : players) {
                PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(p);

                profile.setStatus(PlayerStatus.LOBBY);
                profile.setCurrentQueue(null);
                profile.setQueueData(null);

                GameUtils.resetPlayer(p);

                if (p.getUniqueId().equals(party1.getLeaderUuid()) || p.getUniqueId().equals(party2.getLeaderUuid())) {
                    profile.getPlayer().getInventory().setContents(GameUtils.getPartyLeaderInventory());
                }
                else {
                    profile.getPlayer().getInventory().setContents(GameUtils.getPartyMemberInventory());
                }

                profile.getPlayer().updateInventory();
            }

            return;
        }

        this.playingAmount = this.playingAmount + party1.getPlayers().size() + party2.getPlayers().size();

        TeamMatch match = new TeamMatch(this, this.ladder, arena, MatchType.TWO_VERSUS_TWO, party1.getPlayers(), party2.getPlayers());
        ManagerHandler.getMatchManager().getMatches().put(match.getIdentifier(), match);
    }

    private void startTask() {
        this.queueTask = new BukkitRunnable() {
            public void run() {
                if (playingAmount < 0) {
                    playingAmount = 0;
                }

                Iterator<QueueData> iterator = searchList.iterator();

                while (iterator.hasNext()) {
                    QueueData search = iterator.next();

                    if (!iterator.hasNext()) {
                        continue;
                    }

                    QueueData found = iterator.next();

                    createMatch(search, found);
                }
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 2L);
    }

}