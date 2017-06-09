package me.joeleoli.practice.manager.type;

import lombok.Getter;
import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerKits;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.GameUtils;
import me.joeleoli.practice.util.InventoryUtils;
import me.joeleoli.practice.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KitEditManager implements Listener {

    @Getter private Map<UUID, Ladder> editKits;

    public KitEditManager() {
        this.editKits = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    public void startEditing(Player player, Ladder ladder) {
        this.editKits.put(player.getUniqueId(), ladder);

        ManagerHandler.getEntityHider().hideAllPlayers(player);
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.LOBBY) {
            player.sendMessage(ChatColor.RED + "You must be in the lobby to edit your kits.");
            return;
        }

        profile.setStatus(PlayerStatus.EDITING_KITS);

        GameUtils.resetPlayer(player);
        player.updateInventory();

        if (ladder.getDefaultInventory() != null) {
            if (ladder.getDefaultInventory().getArmorContents() != null) {
                player.getInventory().setArmorContents(ladder.getDefaultInventory().getArmorContents());
            }

            if (ladder.getDefaultInventory().getContents() != null) {
                player.getInventory().setContents(ladder.getDefaultInventory().getContents());
            }
        }

        player.updateInventory();

        ManagerHandler.getConfig().teleportToEditor(player);
        player.sendMessage(ChatColor.GRAY + "You are now kits your " + ChatColor.AQUA + ladder.getName() + ChatColor.GRAY + " kits.");
    }

    private void finishEditing(Player player) {
        this.editKits.remove(player.getUniqueId());

        ManagerHandler.getEntityHider().showAllPlayers(player);
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
        profile.setStatus(PlayerStatus.LOBBY);

        GameUtils.resetPlayer(player);
        player.updateInventory();

        new BukkitRunnable() {
            public void run() {
                player.getInventory().setContents(GameUtils.getLobbyInventory());
                player.updateInventory();
            }
        }.runTaskLater(PracticePlugin.getInstance(), 2L);

        ManagerHandler.getConfig().teleportToSpawn(player);
        player.sendMessage(ChatColor.GRAY + "You have been returned to spawn.");
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!editKits.containsKey(event.getWhoClicked().getUniqueId())) return;

        Inventory inv = event.getClickedInventory();

        if (inv == null) return;

        if (inv.getTitle().equals(ChatColor.RED + "Kit Actions")) {
            if (event.getCurrentItem() == null) return;
            if (event.getCurrentItem().getType().equals(Material.AIR)) return;

            Player player = (Player) event.getWhoClicked();
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            PlayerKits kits = profile.getLadderKits().get(editKits.get(player.getUniqueId()));

            if (kits == null) {
                kits = new PlayerKits();
                profile.getLadderKits().replace(editKits.get(player.getUniqueId()), kits);
            }

            String item = event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase();
            String[] split = item.split(" ");

            if (item.contains("save")) {
                switch(split[2]) {
                    case "#1":
                        kits.setKit1(InventoryUtils.playerInventoryFromPlayer(player));
                        break;
                    case "#2":
                        kits.setKit2(InventoryUtils.playerInventoryFromPlayer(player));
                        break;
                    case "#3":
                        kits.setKit3(InventoryUtils.playerInventoryFromPlayer(player));
                        break;
                    case "#4":
                        kits.setKit4(InventoryUtils.playerInventoryFromPlayer(player));
                        break;
                    case "#5":
                        kits.setKit5(InventoryUtils.playerInventoryFromPlayer(player));
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.GRAY + "You have saved your " + ChatColor.AQUA + editKits.get(player.getUniqueId()).getName() + ChatColor.GRAY + " kit.");
            } else if (item.contains("load")) {
                switch(split[2]) {
                    case "#1":
                        player.getInventory().setContents(kits.getKit1().getContents());
                        player.getInventory().setArmorContents(kits.getKit1().getArmorContents());
                        break;
                    case "#2":
                        player.getInventory().setContents(kits.getKit2().getContents());
                        player.getInventory().setArmorContents(kits.getKit2().getArmorContents());
                        break;
                    case "#3":
                        player.getInventory().setContents(kits.getKit3().getContents());
                        player.getInventory().setArmorContents(kits.getKit3().getArmorContents());
                        break;
                    case "#4":
                        player.getInventory().setContents(kits.getKit4().getContents());
                        player.getInventory().setArmorContents(kits.getKit4().getArmorContents());
                        break;
                    case "#5":
                        player.getInventory().setContents(kits.getKit5().getContents());
                        player.getInventory().setArmorContents(kits.getKit5().getArmorContents());
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.GRAY + "You have loaded your " + ChatColor.AQUA + editKits.get(player.getUniqueId()).getName() + ChatColor.GRAY + " kit.");
            } else if (item.contains("delete")) {
                switch(split[2]) {
                    case "#1":
                        kits.setKit1(null);
                        break;
                    case "#2":
                        kits.setKit2(null);
                        break;
                    case "#3":
                        kits.setKit3(null);
                        break;
                    case "#4":
                        kits.setKit4(null);
                        break;
                    case "#5":
                        kits.setKit5(null);
                        break;
                }

                player.closeInventory();
                player.sendMessage(ChatColor.GRAY + "You have deleted your " + ChatColor.AQUA + editKits.get(player.getUniqueId()).getName() + ChatColor.GRAY + " kit.");
            }

            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getClickedBlock().getType().equals(Material.ANVIL)) {
            PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);
            PlayerKits kits = profile.getLadderKits().get(editKits.get(player.getUniqueId()));

            Inventory inv = Bukkit.createInventory(null, 27, ChatColor.RED + "Kit Actions");

            inv.setItem(0, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GREEN + "Save Kit #1").getItem());
            inv.setItem(2, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GREEN + "Save Kit #2").getItem());
            inv.setItem(4, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GREEN + "Save Kit #3").getItem());
            inv.setItem(6, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GREEN + "Save Kit #4").getItem());
            inv.setItem(8, new ItemBuilder(Material.ENCHANTED_BOOK, ChatColor.GREEN + "Save Kit #5").getItem());

            if (kits != null) {
                if (kits.getKit1() != null) {
                    inv.setItem(9, new ItemBuilder(Material.CHEST, ChatColor.BLUE + "Load Kit #1").getItem());
                    inv.setItem(18, new ItemBuilder(Material.HOPPER, ChatColor.RED + "Delete Kit #1").getItem());
                }

                if (kits.getKit2() != null) {
                    inv.setItem(11, new ItemBuilder(Material.CHEST, ChatColor.BLUE + "Load Kit #2").getItem());
                    inv.setItem(20, new ItemBuilder(Material.HOPPER, ChatColor.RED + "Delete Kit #2").getItem());
                }

                if (kits.getKit3() != null) {
                    inv.setItem(13, new ItemBuilder(Material.CHEST, ChatColor.BLUE + "Load Kit #3").getItem());
                    inv.setItem(22, new ItemBuilder(Material.HOPPER, ChatColor.RED + "Delete Kit #3").getItem());
                }

                if (kits.getKit4() != null) {
                    inv.setItem(15, new ItemBuilder(Material.CHEST, ChatColor.BLUE + "Load Kit #4").getItem());
                    inv.setItem(24, new ItemBuilder(Material.HOPPER, ChatColor.RED + "Delete Kit #4").getItem());
                }

                if (kits.getKit5() != null) {
                    inv.setItem(17, new ItemBuilder(Material.CHEST, ChatColor.BLUE + "Load Kit #5").getItem());
                    inv.setItem(26, new ItemBuilder(Material.HOPPER, ChatColor.RED + "Delete Kit #5").getItem());
                }
            }

            player.openInventory(inv);
            event.setCancelled(true);
        }

        if (event.getClickedBlock().getType().equals(Material.SIGN) || event.getClickedBlock().getType().equals(Material.SIGN_POST) || event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
            GameUtils.resetPlayer(player);
            finishEditing(player);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPearl(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType() == Material.ENDER_PEARL) {
            if (this.editKits.containsKey(event.getPlayer().getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            if (this.editKits.containsKey(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        this.editKits.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (!this.editKits.containsKey(event.getPlayer().getUniqueId())) {
            return;
        }

        this.editKits.remove(event.getPlayer().getUniqueId());
    }

}