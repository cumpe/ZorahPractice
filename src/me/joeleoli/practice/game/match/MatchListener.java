package me.joeleoli.practice.game.match;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.data.DataAccessor;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerKits;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;

import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

public class MatchListener implements Listener {

    public MatchListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            PracticeProfile profile = DataAccessor.getPlayerProfile(player);

            if (profile.getStatus() == PlayerStatus.PLAYING) {
                IMatch match = profile.getCurrentMatch();

                if (!match.getLadder().allowHunger()) {
                    event.setCancelled(true);
                    return;
                }

                if (match.getStartTimestamp() == null) {
                    event.setCancelled(true);
                    return;
                }

                if (System.currentTimeMillis() - match.getStartTimestamp().getTime() < 30000) {
                    event.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (event.getItem() == null) {
            return;
        }

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (!event.getItem().getType().equals(Material.ENCHANTED_BOOK)) {
            return;
        }

        if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).startsWith("Custom Kit")) {
            Integer kitNumber = Integer.valueOf(event.getItem().getItemMeta().getDisplayName().replace("#", "").split(" ")[2]);
            PlayerKits kits = profile.getLadderKits().get(profile.getCurrentMatch().getLadder());

            if (kitNumber == 1) {
                player.getInventory().setContents(kits.getKit1().getContents());
                player.getInventory().setArmorContents(kits.getKit1().getArmorContents());
            }
            else if (kitNumber == 2) {
                player.getInventory().setContents(kits.getKit2().getContents());
                player.getInventory().setArmorContents(kits.getKit2().getArmorContents());
            }
            else if (kitNumber == 3) {
                player.getInventory().setContents(kits.getKit3().getContents());
                player.getInventory().setArmorContents(kits.getKit3().getArmorContents());
            }
            else if (kitNumber == 4) {
                player.getInventory().setContents(kits.getKit4().getContents());
                player.getInventory().setArmorContents(kits.getKit4().getArmorContents());
            }
            else if (kitNumber == 5) {
                player.getInventory().setContents(kits.getKit5().getContents());
                player.getInventory().setArmorContents(kits.getKit5().getArmorContents());
            }
            else {
                player.sendMessage(ChatColor.RED + "That kit could not be found, giving you the default kit.");
            }
            
            player.updateInventory();
        }
        else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).startsWith("Default Kit")) {
            if (profile.getCurrentMatch().getLadder().getDefaultInventory() == null) {
                player.sendMessage(ChatColor.RED + "This ladder does not have a default inventory, use a custom kit.");
                return;
            }

            if (profile.getCurrentMatch().getLadder().getDefaultInventory().getArmorContents() != null) {
                player.getInventory().setContents(profile.getCurrentMatch().getLadder().getDefaultInventory().getContents());
            }

            if (profile.getCurrentMatch().getLadder().getDefaultInventory().getContents() != null) {
                player.getInventory().setArmorContents(profile.getCurrentMatch().getLadder().getDefaultInventory().getArmorContents());
            }
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        player.spigot().respawn();

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.GRAY + " has been slain.");

        event.setDeathMessage(null);
        player.setHealth(20.0);

        for (ItemStack i : event.getDrops()) {
            i.setType(Material.AIR);
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        Projectile entity = event.getEntity();
        IMatch match = profile.getCurrentMatch();
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (match.getPlayers().contains(p)) {
                continue;
            }

            if (match.getSpectators().contains(p.getUniqueId())) {
                continue;
            }

            ManagerHandler.getEntityHider().hideEntity(p, entity);
        }
    }

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();

        if (player.isSprinting()) {
            event.setIntensity((LivingEntity)event.getEntity().getShooter(), 1.0);
        }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        IMatch match = profile.getCurrentMatch();

        if (event.getItemDrop().getItemStack().getType().equals(Material.DIAMOND_SWORD)) {
            event.setCancelled(true);
            return;
        }
        else if (event.getItemDrop().getItemStack().getType().equals(Material.GLASS_BOTTLE)) {
            event.getItemDrop().remove();
            return;
        }
        else if (event.getItemDrop().getItemStack().getType().equals(Material.ENCHANTED_BOOK)) {
            event.setCancelled(true);
            return;
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (match.getPlayers().contains(p)) {
                continue;
            }

            if (match.getSpectators().contains(p.getUniqueId())) {
                continue;
            }

            ManagerHandler.getEntityHider().hideEntity(p, event.getItemDrop());
        }

        new BukkitRunnable() {
            public void run() {
                if (event.getItemDrop() != null) {
                    event.getItemDrop().remove();
                }
            }
        }.runTaskLater(PracticePlugin.getInstance(), 20L * 6);
    }
    
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player)event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        if (profile.getCurrentMatch().getMatchStatus() == MatchStatus.STARTING) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        IMatch match = profile.getCurrentMatch();

        if (match.getMatchStatus() == MatchStatus.STARTING) {
            event.setCancelled(true);
            return;
        }

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            PracticeProfile damagerProfile = ManagerHandler.getPlayerManager().getPlayerProfile(damager);

            if (damagerProfile.getStatus() != PlayerStatus.PLAYING) {
                event.setCancelled(true);
                return;
            }

            if (match.isDead(damager)) {
                event.setCancelled(true);
                return;
            }

            if (match.getTeam(player).contains(damager)) {
                event.setCancelled(true);
                return;
            }
            
            if (player.getHealth() - event.getFinalDamage() <= 0.0) {
            	player.setHealth(20);
            	match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.GRAY + " has been slain by " + ChatColor.GREEN + damager.getName() + ChatColor.GRAY + ".");
            }
        } else if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow)event.getDamager();

            if (arrow.getShooter() instanceof Player) {
                Player damager = (Player)arrow.getShooter();
                
                if (match.getMatchType() == MatchType.PARTY_VERSUS_PARTY || match.getMatchType() == MatchType.TWO_VERSUS_TWO) {
                	if (match.getTeam(player).contains(damager)) {
                		event.setCancelled(true);
                		return;
                	}
                }

                double healthDisplay;

                if (player.getHealth() - event.getFinalDamage() <= 0.0) {
                    healthDisplay = 0.0;
                }
                else {
                    healthDisplay = (player.getHealth() - event.getFinalDamage()) / 2.0;
                }

                NumberFormat formatter = new DecimalFormat("#0.0");
                String newFormat = formatter.format(healthDisplay);
                damager.sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.AQUA + " is now at " + ChatColor.RED + newFormat + ChatColor.DARK_RED + " " + StringEscapeUtils.unescapeJava("\u2764"));
            
                if (player.getHealth() - event.getFinalDamage() <= 0.0) {
                	player.setHealth(20);
                	match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.GRAY + " has been shot by " + ChatColor.GREEN + damager.getName() + ChatColor.GRAY + ".");
                }
            }
        }
        
        if (player.getHealth() - event.getFinalDamage() <= 0.0) {
        	match.handleDeath(player, player.getLocation(), ChatColor.RED + player.getName() + ChatColor.GRAY + " has been slain.");
        }
    }

    @EventHandler
    public void onEat(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        if (!event.getItem().getItemMeta().hasDisplayName()) {
            return;
        }

        if (event.getItem().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Golden Head")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 240, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
        }
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        if (!profile.getCurrentMatch().getLadder().allowHeal()) {
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) event.setCancelled(true);
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.REGEN) event.setCancelled(true);
        }
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, null, ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the match.");
    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (profile.getStatus() != PlayerStatus.PLAYING) {
            return;
        }

        IMatch match = profile.getCurrentMatch();
        match.handleDeath(player, null, ChatColor.RED + player.getName() + ChatColor.GRAY + " has left the match.");
    }

}