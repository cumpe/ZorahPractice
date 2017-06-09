package me.joeleoli.practice.game.queue.type;

import lombok.Getter;
import lombok.Setter;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.arena.Arena;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.type.SoloMatch;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.QueueData;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerElo;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.GameUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

public class SoloQueue implements IQueue {

    @Getter private UUID identifier;
    @Getter private Ladder ladder;

    @Getter private boolean ranked;

    @Getter @Setter private int playingAmount = 0;

    @Getter private LinkedList<QueueData> searchList;
    @Getter private BukkitTask queueTask;
    
    public SoloQueue(Ladder ladder, Boolean isRanked) {
        this.identifier = UUID.randomUUID();
        this.ladder = ladder;
        this.ranked = isRanked;
        this.searchList = new LinkedList<>();
        this.startTask();
    }

    @Override
    public String getName() {
        return this.ladder.getName() + " 1v1";
    }

    @Override
    public int getQueueingAmount() {
        return this.searchList.size();
    }

    @Override
    public void addToQueue(Object object) {
        Player player = (Player) object;
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
        PlayerElo rating = profile.getLadderRatings().get(this.ladder);

        if (rating == null) {
            rating = new PlayerElo(1000);
        }

        QueueData participant = new QueueData(player.getUniqueId(), this, rating.toInteger());

        this.searchList.offer(participant);

        profile.setCurrentQueue(this);
        profile.setQueueData(participant);
        profile.setStatus(PlayerStatus.QUEUEING);

        player.sendMessage(ChatColor.AQUA + (ranked ? "[Ranked] " : "[Unranked] ") + ChatColor.GRAY + "You have been added to the " + ChatColor.AQUA + this.ladder.getName() + " 1v1 " + ChatColor.GRAY + "queue, please wait while we find you an opponent.");

        if (player.getGameMode() == GameMode.CREATIVE) {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @Override
    public void removeFromQueue(Object object) {
        this.removeFromQueue(object, true, true, true);
    }

    private void removeFromQueue(Object object, boolean send, boolean clean, boolean teleport) {
        this.searchList.remove(object);

        QueueData participant = (QueueData) object;
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile((UUID) participant.getObject());

        profile.setStatus(PlayerStatus.LOBBY);
        profile.setCurrentQueue(null);
        profile.setQueueData(null);

        if (clean) {
            GameUtils.resetPlayer(profile.getPlayer());
            profile.getPlayer().getInventory().setContents(GameUtils.getLobbyInventory());
            profile.getPlayer().updateInventory();
        }

        if (teleport) {
            ManagerHandler.getConfig().teleportToSpawn(profile.getPlayer());
        }

        if (send) {
            profile.getPlayer().sendMessage(ChatColor.GRAY + "You have been removed from the queue.");
        }
    }
    
    private boolean inRange(QueueData data1, QueueData data2) {
        return (data1.getRating() >= data2.getMinRange() && data1.getRating() <= data2.getMaxRange()) || (data2.getRating() >= data1.getMinRange() && data2.getRating() <= data1.getMaxRange());
    }
    
    private void incrementRange(QueueData data) {
        data.incrementRange();
        Bukkit.getPlayer((UUID)data.getObject()).sendMessage(ChatColor.GRAY + "Searching in ranges " + ChatColor.AQUA + "[" + data.getMinRange() + "->" + data.getMaxRange() + "]");
    }
    
    private void createMatch(QueueData data1, QueueData data2) {
        Arena arena = ManagerHandler.getArenaManager().getRandomArena();

        this.removeFromQueue(data1, false, true, false);
        this.removeFromQueue(data2, false, true, false);

        Player player1 = Bukkit.getPlayer((UUID) data1.getObject());
        Player player2 = Bukkit.getPlayer((UUID) data2.getObject());

        if (arena == null) {
            player1.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");
            player2.sendMessage(ChatColor.RED + "There are no available arenas, you have been removed from the queue.");
            return;
        }

        SoloMatch match = new SoloMatch(this, this.ladder, arena, this.ranked, player1, player2);
        ManagerHandler.getMatchManager().getMatches().put(match.getIdentifier(), match);

        this.playingAmount = this.playingAmount + 2;
    }
    
    private void startTask() {
        this.queueTask = new BukkitRunnable() {
            int i = 0;
            
            public void run() {
                if (playingAmount < 0) {
                    playingAmount = 0;
                }

                Iterator<QueueData> iterator = SoloQueue.this.searchList.iterator();

                while (iterator.hasNext()) {
                    QueueData search = iterator.next();

                    if (i == 100 && ranked) {
                        incrementRange(search);
                    }

                    if (!iterator.hasNext()) {
                        continue;
                    }

                    QueueData found = iterator.next();

                    if (ranked) {
                        if (inRange(search, found)) {
                            createMatch(search, found);
                        }
                    }
                    else {
                        createMatch(search, found);
                    }
                }

                if (this.i >= 100) {
                    this.i = 0;
                }
                else {
                    this.i = this.i + 2;
                }
            }
        }.runTaskTimer(PracticePlugin.getInstance(), 0L, 2L);
    }

}