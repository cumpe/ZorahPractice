package me.joeleoli.practice;

import lombok.Getter;

import me.joeleoli.practice.command.PluginCommand;
import me.joeleoli.practice.command.type.*;
import me.joeleoli.practice.command.type.variable.VariableCommand;
import me.joeleoli.practice.game.arena.ArenaCommand;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.game.duel.DuelCommand;
import me.joeleoli.practice.game.ladder.LadderCommand;
import me.joeleoli.practice.game.match.MatchListener;
import me.joeleoli.practice.game.party.PartyCommand;
import me.joeleoli.practice.game.queue.QueueListener;
import me.joeleoli.practice.listener.*;
import me.joeleoli.practice.util.GameUtils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class PracticePlugin extends JavaPlugin {

    @Getter public static PracticePlugin instance;

    public void onEnable() {
        instance = this;

        new ManagerHandler(this);
        new Cache();

        this.loadListeners();

        Map<String, PluginCommand> commands = new HashMap<>();
        commands.put("practice", new PracticeCommand(this));
        commands.put("ladder", new LadderCommand(this));
        commands.put("arena", new ArenaCommand(this));
        commands.put("party", new PartyCommand(this));
        commands.put("duel", new DuelCommand(this));
        commands.put("partyduel", new PartyDuelCommand(this));
        commands.put("inventory", new InventoryCommand(this));
        commands.put("spectate", new SpectateCommand(this));
        commands.put("savedata", new SaveDataCommand(this));
        commands.put("cancel", new CancelCommand(this));
        commands.put("statistics", new StatisticsCommand(this));
        commands.put("day", new DayCommand(this));
        commands.put("night", new NightCommand(this));
        commands.put("variable", new VariableCommand(this));
        commands.put("changelog", new ChangeLogCommand(this));
        commands.put("ping", new PingCommand(this));
        commands.forEach((name, command) -> this.getCommand(name).setExecutor(command));

        Bukkit.setWhitelist(true);

        for (Player player : Bukkit.getOnlinePlayers()) {
            GameUtils.resetPlayer(player);
            player.getInventory().setContents(GameUtils.getLobbyInventory());
            player.updateInventory();
            player.setMaximumNoDamageTicks(19);

            ManagerHandler.getConfig().teleportToSpawn(player);
        }

        new BukkitRunnable() {
            public void run() {
                for (World world : Bukkit.getServer().getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Item) {
                            entity.remove();
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0L, 20L * 30);

        new BukkitRunnable() {
            public void run() {
                Bukkit.setWhitelist(false);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "knockback 0.97 0.9375 0.97");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setprefix famous &5<10031>&2&o");
            }
        }.runTaskLater(PracticePlugin.getInstance(), 20L * 3);
    }

    public void onDisable() {
        ManagerHandler.saveData();
    }

    private void loadListeners() {
        new PearlFix();
        new QueueListener();
        new MatchListener();
        new PlayerListener();
        new InventoryListener();
        new EnvironmentListener();
        new PacketListener();
    }

}