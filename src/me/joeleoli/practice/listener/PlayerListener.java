package me.joeleoli.practice.listener;

import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.event.PlayerTabCreateEvent;

import me.joeleoli.practice.PracticeConfiguration;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.game.cache.Cache;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.MatchStatus;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.event.PlayerExitQueueEvent;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.menu.*;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.scoreboard.PlayerBoard;
import me.joeleoli.practice.util.GameUtils;
import me.joeleoli.practice.util.MessageUtils;
import me.joeleoli.practice.util.PingUtil;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;

public class PlayerListener implements Listener {

    public PlayerListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if ((event.getMessage().startsWith("@") || event.getMessage().startsWith("!")) && profile.getParty() != null) {
            profile.getParty().sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.GRAY + ": " + event.getMessage().substring(1, event.getMessage().length()));
            event.setCancelled(true);
            return;
        }

        for (Player p : event.getRecipients()) {
            p.sendMessage(player.getDisplayName() + ChatColor.GRAY + ": " + event.getMessage());
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        player.setMaximumNoDamageTicks(19);

        GameUtils.resetPlayer(player);

        player.getInventory().setContents(GameUtils.getLobbyInventory());
        player.updateInventory();

        ManagerHandler.getConfig().teleportToSpawn(player);

        player.setCanPickupItems(false);

        if (player.getGameMode() != GameMode.CREATIVE) {
            player.setAllowFlight(false);
        }

        player.spigot().setCollidesWithEntities(false);

        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "---------------------------------------------");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " * " + ChatColor.GRAY + "Welcome to " + ChatColor.AQUA + "" + ChatColor.BOLD + "PvPTemple" + ChatColor.GRAY + "!");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " * " + ChatColor.GRAY + "TeamSpeak: " + ChatColor.AQUA + "ts.pvptemple.it:1919");
        player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + " * " + ChatColor.GRAY + "Store: " + ChatColor.AQUA + "store.pvptemple.it");
        player.sendMessage(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "---------------------------------------------");

        ManagerHandler.getScoreboardHandler().applyBoard(player);

        for (PlayerBoard board : ManagerHandler.getScoreboardHandler().getPlayerBoards().values()) {
            board.addUpdate(player);
        }

        new BukkitRunnable() {
            public void run() {
                ManagerHandler.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(Bukkit.getOnlinePlayers());
            }
        }.runTaskLater(PracticePlugin.getInstance(), 4L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() == PlayerStatus.QUEUEING) {
            PlayerExitQueueEvent queueEvent = new PlayerExitQueueEvent(player, profile.getCurrentQueue());
            Bukkit.getPluginManager().callEvent(queueEvent);
        }
        else if (profile.getStatus() == PlayerStatus.SPECTATING) {
            ManagerHandler.getSpectateManager().stopSpectating(player, false);
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile != null) {
            if (profile.getStatus() == PlayerStatus.QUEUEING) {
                PlayerExitQueueEvent queueEvent = new PlayerExitQueueEvent(player, profile.getCurrentQueue());
                Bukkit.getPluginManager().callEvent(queueEvent);
            }
            else if (profile.getStatus() == PlayerStatus.SPECTATING) {
                ManagerHandler.getSpectateManager().stopSpectating(player, false);
            }
        }

        event.setLeaveMessage(null);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = DataAccessor.getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() == null) {
                return;
            }

            if (event.getItem().getType().equals(Material.AIR)) {
                return;
            }

            if (event.getItem().getItemMeta().getDisplayName() == null) {
                return;
            }

            Player player = event.getPlayer();
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

            if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.SETTINGS.getItem().getItemMeta().getDisplayName())) {
                player.performCommand("settings");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.RANKED_QUEUE.getItem().getItemMeta().getDisplayName())) {
                RankedQueueMenu.open(player);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.UNRANKED_QUEUE.getItem().getItemMeta().getDisplayName())) {
                UnrankedQueueMenu.open(player);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.PARTY_QUEUE.getItem().getItemMeta().getDisplayName())) {
                PartyQueueMenu.open(player);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.CREATE_PARTY.getItem().getItemMeta().getDisplayName())) {
                Bukkit.dispatchCommand(player, "party create");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.DISBAND_PARTY.getItem().getItemMeta().getDisplayName())) {
                Bukkit.dispatchCommand(player, "party disband");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.LEAVE_PARTY.getItem().getItemMeta().getDisplayName())) {
                Bukkit.dispatchCommand(player, "party leave");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.PARTY_INFORMATION.getItem().getItemMeta().getDisplayName())) {
                Bukkit.dispatchCommand(player, "party info");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.VIEW_PARTIES.getItem().getItemMeta().getDisplayName())) {
                Bukkit.dispatchCommand(player, "party list");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.PARTY_EVENTS.getItem().getItemMeta().getDisplayName())) {
                PartyEventMenu.open(player);
                event.setCancelled(true);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.KIT_EDTIOR.getItem().getItemMeta().getDisplayName())) {
                KitsMenu.open(player);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.LEAVE_QUEUE.getItem().getItemMeta().getDisplayName())) {
                PlayerExitQueueEvent queueEvent = new PlayerExitQueueEvent(player, profile.getCurrentQueue());
                Bukkit.getPluginManager().callEvent(queueEvent);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.STOP_SPECTATING.getItem().getItemMeta().getDisplayName())) {
                ManagerHandler.getSpectateManager().stopSpectating(player, true);
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.FFA.getItem().getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.RED + "This feature is coming soon!");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.SPECTATOR_MODE.getItem().getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.RED + "This feature is coming soon!");
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.QUEUE_INFO.getItem().getItemMeta().getDisplayName())) {
                if (profile.getStatus() == PlayerStatus.QUEUEING) {
                    IQueue queue = profile.getCurrentQueue();
                    player.sendMessage(ChatColor.GRAY + "You are in the " + ChatColor.AQUA + queue.getName() + ChatColor.GRAY + " queue waiting for an opponent.");
                }
                else {
                    player.sendMessage(ChatColor.RED + "You are not in a queue.");
                }
            }
            else if (event.getItem().getItemMeta().getDisplayName().equals(GameUtils.PlayerItem.SPECTATOR_INFO.getItem().getItemMeta().getDisplayName())) {
                player.sendMessage(ChatColor.GOLD + "You are currently spectating the following players:");

                String players = "";

                for (Player p : profile.getSpectatingMatch().getPlayers()) {
                    players += p.getDisplayName() + ", ";
                }

                player.sendMessage(ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + players.substring(0, players.length() - 2));
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().getLocation().getBlockY() < -5) {
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(event.getPlayer());

            if (profile.getStatus() == PlayerStatus.PLAYING) {
                return;
            }

            if (profile.getStatus() == PlayerStatus.EDITING_KITS) {
                ManagerHandler.getConfig().teleportToEditor(event.getPlayer());
            }
            else {
                ManagerHandler.getConfig().teleportToSpawn(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() == PlayerStatus.PLAYING) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (!((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (!((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() == PlayerStatus.PLAYING) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() == PlayerStatus.PLAYING) {
            return;
        }

        if ((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() == PlayerStatus.EDITING_KITS || profile.getStatus() == PlayerStatus.PLAYING) {
            return;
        }

        if ((player.hasPermission("practice.admin") || player.isOp()) && player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPearl(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (profile.getStatus() != PlayerStatus.PLAYING) {
                event.setCancelled(true);
                return;
            }

            if (profile.getCurrentMatch().getMatchStatus() == MatchStatus.STARTING) {
                player.sendMessage(ChatColor.RED + "Pearl" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "You cannot throw an enderpearl until the match has started.");
                event.setCancelled(true);
                return;
            }

            if (profile.getEnderpearlTimer().isActive()) {
                DecimalFormat format = new DecimalFormat("##.#");
                String timeLeft = format.format(profile.getEnderpearlTimer().getTimeLeft() / 1000.0);
                player.sendMessage(ChatColor.RED + "Pearl" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "You cannot throw an enderpearl for another " + ChatColor.GREEN + timeLeft + "s" + ChatColor.GRAY + ".");
                event.setCancelled(true);
                return;
            }

            profile.getEnderpearlTimer().setTimerEnd(System.currentTimeMillis() + 16000L);

            new BukkitRunnable() {
                public void run() {
                    if (player != null && player.isOnline()) {
                        PracticeProfile prof = ManagerHandler.getPlayerManager().getPlayerProfile(player);

                        if (prof == null) {
                            return;
                        }

                        if (prof.getStatus() == PlayerStatus.PLAYING || prof.getStatus() == PlayerStatus.FFA) {
                            player.sendMessage(ChatColor.RED + "Pearl" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "You are no longer on enderpearl cooldown.");
                        }
                    }
                }
            }.runTaskLater(PracticePlugin.getInstance(), 20L * 16);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        for (ItemStack i : event.getDrops()) {
            i.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        event.setRespawnLocation(ManagerHandler.getConfig().getSpawnPoint());

        new BukkitRunnable() {
            public void run() {
                if (profile.getParty() == null) {
                    player.getInventory().setContents(GameUtils.getLobbyInventory());
                }
                else {
                    if (player.equals(profile.getParty().getLeader())) {
                        player.getInventory().setContents(GameUtils.getPartyLeaderInventory());
                    }
                    else {
                        player.getInventory().setContents(GameUtils.getPartyMemberInventory());
                    }
                }
            }
        }.runTaskLater(PracticePlugin.getInstance(), 2L);
    }

    private String translate(Player player, String path) {
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        String display = ManagerHandler.getConfig().getRootConfig().getConfig().getString(path);

        if (display.contains("%online_players%")) {
            display = display.replace("%online_players%", Bukkit.getServer().getOnlinePlayers().size() + "");
        }
        else if (display.contains("%player_ping%")) {
            display = display.replace("%player_ping%", PingUtil.getPing(player) + "");
        }
        else if (display.contains("%playing_amount%")) {
            display = display.replace("%playing_amount%", Cache.playingAmount + "");
        }
        else if (display.contains("%queuing_amount%")) {
            display = display.replace("%queuing_amount%", Cache.queueingAmount + "");
        }
        else if (display.contains("%spectating_amount%")) {
            display = display.replace("%queuing_amount%", Cache.spectatingAmount + "");
        }
        else if (display.contains("_rating%")) {
            String ladderName = display.substring(3, display.length() - 8);
            Ladder ladder = ManagerHandler.getLadderManager().getLadders().get(ladderName);

            if (ladder == null) {
                display = display.replace("%" + ladderName + "_rating%", "Not Found");
            }
            else {
                display = display.replace("%" + ladderName + "_rating%", profile.getLadderRatings().get(ladder).toInteger() + "");
            }
        }
        else if (display.contains("%globalrating%")) {
            display = display.replace("%globalrating%", profile.getGlobalRating() + "");
        }
        else if (display.contains("%globalrank%")) {
            display = display.replace("%globalrank%", Cache.rankings.containsKey(player.getUniqueId()) ? Cache.rankings.get(player.getUniqueId()) + "" : "Over #500");
        }
        else if (display.contains("%matches_played%")) {
            display = display.replace("%matches_played%", profile.getMatchesPlayed() + "");
        }
        else if (display.contains("%ranked_wins%")) {
            display = display.replace("%ranked_wins%", profile.getRankedWins() + "");
        }
        else if (display.contains("%ranked_losses%")) {
            display = display.replace("%ranked_losses%", profile.getRankedLosses() + "");
        }
        else if (display.contains("%unranked_wins%")) {
            display = display.replace("%unranked_wins%", profile.getUnrankedWins() + "");
        }
        else if (display.contains("%unranked_losses%")) {
            display = display.replace("%unranked_losses%", profile.getUnrankedLosses() + "");
        }

        return MessageUtils.color(display);
    }

    private void updateTab(PlayerTab playerTab) throws Exception {
        playerTab.getByPosition(0, 0).text(translate(playerTab.getPlayer(), "tab.strings.1")).send();
        playerTab.getByPosition(0, 1).text(translate(playerTab.getPlayer(), "tab.strings.2")).send();
        playerTab.getByPosition(0, 2).text(translate(playerTab.getPlayer(), "tab.strings.3")).send();
        playerTab.getByPosition(0, 3).text(translate(playerTab.getPlayer(), "tab.strings.4")).send();
        playerTab.getByPosition(0, 4).text(translate(playerTab.getPlayer(), "tab.strings.5")).send();
        playerTab.getByPosition(0, 5).text(translate(playerTab.getPlayer(), "tab.strings.6")).send();
        playerTab.getByPosition(0, 6).text(translate(playerTab.getPlayer(), "tab.strings.7")).send();
        playerTab.getByPosition(0, 7).text(translate(playerTab.getPlayer(), "tab.strings.8")).send();
        playerTab.getByPosition(0, 8).text(translate(playerTab.getPlayer(), "tab.strings.9")).send();
        playerTab.getByPosition(0, 9).text(translate(playerTab.getPlayer(), "tab.strings.10")).send();
        playerTab.getByPosition(0, 10).text(translate(playerTab.getPlayer(), "tab.strings.11")).send();
        playerTab.getByPosition(0, 11).text(translate(playerTab.getPlayer(), "tab.strings.12")).send();
        playerTab.getByPosition(0, 12).text(translate(playerTab.getPlayer(), "tab.strings.13")).send();
        playerTab.getByPosition(0, 13).text(translate(playerTab.getPlayer(), "tab.strings.14")).send();
        playerTab.getByPosition(0, 14).text(translate(playerTab.getPlayer(), "tab.strings.15")).send();
        playerTab.getByPosition(0, 15).text(translate(playerTab.getPlayer(), "tab.strings.16")).send();
        playerTab.getByPosition(0, 16).text(translate(playerTab.getPlayer(), "tab.strings.17")).send();
        playerTab.getByPosition(0, 17).text(translate(playerTab.getPlayer(), "tab.strings.18")).send();
        playerTab.getByPosition(0, 18).text(translate(playerTab.getPlayer(), "tab.strings.19")).send();
        playerTab.getByPosition(0, 19).text(translate(playerTab.getPlayer(), "tab.strings.20")).send();
        playerTab.getByPosition(1, 0).text(translate(playerTab.getPlayer(), "tab.strings.21")).send();
        playerTab.getByPosition(1, 1).text(translate(playerTab.getPlayer(), "tab.strings.22")).send();
        playerTab.getByPosition(1, 2).text(translate(playerTab.getPlayer(), "tab.strings.23")).send();
        playerTab.getByPosition(1, 3).text(translate(playerTab.getPlayer(), "tab.strings.24")).send();
        playerTab.getByPosition(1, 4).text(translate(playerTab.getPlayer(), "tab.strings.25")).send();
        playerTab.getByPosition(1, 5).text(translate(playerTab.getPlayer(), "tab.strings.26")).send();
        playerTab.getByPosition(1, 6).text(translate(playerTab.getPlayer(), "tab.strings.27")).send();
        playerTab.getByPosition(1, 7).text(translate(playerTab.getPlayer(), "tab.strings.28")).send();
        playerTab.getByPosition(1, 8).text(translate(playerTab.getPlayer(), "tab.strings.29")).send();
        playerTab.getByPosition(1, 9).text(translate(playerTab.getPlayer(), "tab.strings.30")).send();
        playerTab.getByPosition(1, 10).text(translate(playerTab.getPlayer(), "tab.strings.31")).send();
        playerTab.getByPosition(1, 11).text(translate(playerTab.getPlayer(), "tab.strings.32")).send();
        playerTab.getByPosition(1, 12).text(translate(playerTab.getPlayer(), "tab.strings.33")).send();
        playerTab.getByPosition(1, 13).text(translate(playerTab.getPlayer(), "tab.strings.34")).send();
        playerTab.getByPosition(1, 14).text(translate(playerTab.getPlayer(), "tab.strings.35")).send();
        playerTab.getByPosition(1, 15).text(translate(playerTab.getPlayer(), "tab.strings.36")).send();
        playerTab.getByPosition(1, 16).text(translate(playerTab.getPlayer(), "tab.strings.37")).send();
        playerTab.getByPosition(1, 17).text(translate(playerTab.getPlayer(), "tab.strings.38")).send();
        playerTab.getByPosition(1, 18).text(translate(playerTab.getPlayer(), "tab.strings.39")).send();
        playerTab.getByPosition(1, 19).text(translate(playerTab.getPlayer(), "tab.strings.40")).send();
        playerTab.getByPosition(2, 0).text(translate(playerTab.getPlayer(), "tab.strings.41")).send();
        playerTab.getByPosition(2, 1).text(translate(playerTab.getPlayer(), "tab.strings.42")).send();
        playerTab.getByPosition(2, 2).text(translate(playerTab.getPlayer(), "tab.strings.43")).send();
        playerTab.getByPosition(2, 3).text(translate(playerTab.getPlayer(), "tab.strings.44")).send();
        playerTab.getByPosition(2, 4).text(translate(playerTab.getPlayer(), "tab.strings.45")).send();
        playerTab.getByPosition(2, 5).text(translate(playerTab.getPlayer(), "tab.strings.46")).send();
        playerTab.getByPosition(2, 6).text(translate(playerTab.getPlayer(), "tab.strings.47")).send();
        playerTab.getByPosition(2, 7).text(translate(playerTab.getPlayer(), "tab.strings.48")).send();
        playerTab.getByPosition(2, 8).text(translate(playerTab.getPlayer(), "tab.strings.49")).send();
        playerTab.getByPosition(2, 9).text(translate(playerTab.getPlayer(), "tab.strings.50")).send();
        playerTab.getByPosition(2, 10).text(translate(playerTab.getPlayer(), "tab.strings.51")).send();
        playerTab.getByPosition(2, 11).text(translate(playerTab.getPlayer(), "tab.strings.52")).send();
        playerTab.getByPosition(2, 12).text(translate(playerTab.getPlayer(), "tab.strings.53")).send();
        playerTab.getByPosition(2, 13).text(translate(playerTab.getPlayer(), "tab.strings.54")).send();
        playerTab.getByPosition(2, 14).text(translate(playerTab.getPlayer(), "tab.strings.55")).send();
        playerTab.getByPosition(2, 15).text(translate(playerTab.getPlayer(), "tab.strings.56")).send();
        playerTab.getByPosition(2, 16).text(translate(playerTab.getPlayer(), "tab.strings.57")).send();
        playerTab.getByPosition(2, 17).text(translate(playerTab.getPlayer(), "tab.strings.58")).send();
        playerTab.getByPosition(2, 18).text(translate(playerTab.getPlayer(), "tab.strings.59")).send();
        playerTab.getByPosition(2, 19).text(translate(playerTab.getPlayer(), "tab.strings.60")).send();
    }

    @EventHandler
    public void onPlayerTabCreateEvent(PlayerTabCreateEvent event) {
        if (!PracticeConfiguration.USE_TAB) {
            return;
        }

        PlayerTab playerTab = event.getPlayerTab();

        new BukkitRunnable() {
            public void run() {
                if (playerTab == null || playerTab.getPlayer() == null || !playerTab.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }

                try {
                    updateTab(playerTab);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.cancel();
                    playerTab.getPlayer().sendMessage(ChatColor.RED + "Failed to load your tab info, re-join if you want to re-attempt to load it.");
                }
            }
        }.runTaskTimerAsynchronously(PracticePlugin.getInstance(), 0L, 10L);
    }

}