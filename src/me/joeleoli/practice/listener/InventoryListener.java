package me.joeleoli.practice.listener;

import me.joeleoli.practice.PracticePlugin;
import me.joeleoli.practice.game.duel.DuelRequest;
import me.joeleoli.practice.game.ladder.Ladder;
import me.joeleoli.practice.game.match.MatchType;
import me.joeleoli.practice.game.match.type.FfaMatch;
import me.joeleoli.practice.game.match.type.TeamMatch;
import me.joeleoli.practice.game.party.Party;
import me.joeleoli.practice.game.party.PartyEventType;
import me.joeleoli.practice.game.party.PartyStatus;
import me.joeleoli.practice.game.queue.IQueue;
import me.joeleoli.practice.game.queue.event.PlayerEnterQueueEvent;
import me.joeleoli.practice.manager.ManagerHandler;
import me.joeleoli.practice.player.PlayerStatus;
import me.joeleoli.practice.player.PracticeProfile;
import me.joeleoli.practice.util.HiddenStringUtil;
import me.joeleoli.practice.util.ItemBuilder;
import me.joeleoli.practice.util.MessageUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class InventoryListener implements Listener {

    private static Map<UUID, UUID> selectedParty = new HashMap<>();
    public static Map<UUID, UUID> selectedPlayer = new HashMap<>();
    private static Map<UUID, PartyEventType> selectedEvent = new HashMap<>();

    public InventoryListener() {
        Bukkit.getPluginManager().registerEvents(this, PracticePlugin.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getClickedInventory();
        Player player = (Player)event.getWhoClicked();
        PracticeProfile profile = ManagerHandler.getPlayerManager().getPlayerProfile(player);

        if (inv == null) {
            return;
        }

        if (inv.getTitle().equals(ChatColor.GREEN + "Ranked Queue")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != PlayerStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the lobby to join a queue.");
                player.closeInventory();
                event.setCancelled(true);
            }

            String hidden = HiddenStringUtil.extractHiddenString(event.getCurrentItem().getItemMeta().getLore().get(5));
            IQueue queue = ManagerHandler.getQueueManager().getQueues().get(UUID.fromString(hidden));

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Could not find that queue.");
                return;
            }

            queue.addToQueue(player);

            PlayerEnterQueueEvent queueEvent = new PlayerEnterQueueEvent(player, queue);
            Bukkit.getPluginManager().callEvent(queueEvent);

            player.closeInventory();
            event.setCancelled(true);
        }
        else if (inv.getTitle().equals(ChatColor.GREEN + "Unranked Queue")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != PlayerStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the lobby to join a queue.");
                player.closeInventory();
                event.setCancelled(true);
            }

            String hidden = HiddenStringUtil.extractHiddenString(event.getCurrentItem().getItemMeta().getLore().get(4));
            IQueue queue = ManagerHandler.getQueueManager().getQueues().get(UUID.fromString(hidden));

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Could not find that queue.");
                return;
            }

            queue.addToQueue(player);

            PlayerEnterQueueEvent queueEvent = new PlayerEnterQueueEvent(player, queue);
            Bukkit.getPluginManager().callEvent(queueEvent);

            player.closeInventory();
            event.setCancelled(true);
        }
        else if (inv.getTitle().startsWith(ChatColor.GRAY + "Viewing the inventory of ")) {
            event.setCancelled(true);
        }
        else if (inv.getTitle().equals(ChatColor.GREEN + "2v2 Queue")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getStatus() != PlayerStatus.LOBBY) {
                player.sendMessage(ChatColor.RED + "You need to be in the lobby to join a queue.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            if (profile.getParty() == null) {
                player.sendMessage(ChatColor.RED + "You must have a party to open this menu.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            if (profile.getParty().getPlayers().size() != 2) {
                profile.getParty().sendMessage(ChatColor.RED + "Tried to put this party in 2v2 queue, but the party size doesn't equal 2 players.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            String hidden = HiddenStringUtil.extractHiddenString(event.getCurrentItem().getItemMeta().getLore().get(4));
            IQueue queue = ManagerHandler.getQueueManager().getQueues().get(UUID.fromString(hidden));

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "Could not find that queue.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            queue.addToQueue(profile.getParty());

            PlayerEnterQueueEvent queueEvent = new PlayerEnterQueueEvent(player, queue);
            Bukkit.getPluginManager().callEvent(queueEvent);

            player.closeInventory();
            event.setCancelled(true);
        }
        else if (inv.getTitle().equals(ChatColor.RED + "Kit Editor")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            Ladder ladder = ManagerHandler.getLadderManager().getLadders().get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            player.closeInventory();
            event.setCancelled(true);

            if (ladder == null) {
                player.sendMessage(ChatColor.RED + "That ladder could not be found.");
                return;
            }

            ManagerHandler.getKitEditManager().startEditing(player, ladder);
        }
        else if (inv.getTitle().equals(ChatColor.GOLD + "Party List")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getParty() == null) {
                player.sendMessage(ChatColor.RED + "You must have a party to do that.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            if (profile.getParty().getLeader().equals(player)) {
                String playerName = event.getCurrentItem().getItemMeta().toString().split(" ")[3];
                Player partyLeader = Bukkit.getPlayer(playerName.substring(0, playerName.length() - 1));

                if (partyLeader == null) {
                    player.sendMessage(ChatColor.RED + "That party is no longer available to be dueled.");
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }

                Party party = ManagerHandler.getPlayerManager().getPlayerProfile(partyLeader).getParty();

                if (party.getStatus() != PartyStatus.IDLE) {
                    player.sendMessage(ChatColor.RED + "That party is no longer available to be dueled.");
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }

                player.closeInventory();

                selectedParty.put(player.getUniqueId(), party.getIdentifier());

                Inventory newInv = Bukkit.createInventory(null, ManagerHandler.getLadderManager().getLadderAmount(), ChatColor.GOLD + "Send Party Duel Request");

                if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
                    for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                        newInv.setItem(ladder.getDisplayOrder(), new ItemBuilder(ladder.getDisplayIcon().getType(), MessageUtils.color(ladder.getDisplayName()), 1, ladder.getDisplayIcon().getDurability(),ChatColor.GRAY + "Click to select.").getItem());
                    }
                }

                player.openInventory(newInv);

                event.setCancelled(true);
            } else {
                Player partyLeader = Bukkit.getPlayer(event.getCurrentItem().getItemMeta().toString().split(" ")[2]);

                if (partyLeader == null) {
                    player.sendMessage(ChatColor.RED + "That party is no longer available.");
                    player.closeInventory();
                    event.setCancelled(true);
                    return;
                }

                Party party = ManagerHandler.getPlayerManager().getPlayerProfile(partyLeader).getParty();

                String msg = ChatColor.GOLD + "Players: " + ChatColor.YELLOW + "";

                Iterator<Player> iterator = party.getPlayers().iterator();

                while(iterator.hasNext()) {
                    Player p = iterator.next();

                    if(p == null) {
                        iterator.remove();
                        continue;
                    }

                    msg += p.getName() + ", ";
                }

                player.sendMessage(msg.substring(0, msg.length() - 2));

                player.closeInventory();
                event.setCancelled(true);
            }
        }
        else if (inv.getTitle().equals(ChatColor.GOLD + "Send Party Duel Request")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getParty() == null) {
                player.sendMessage(ChatColor.RED + "You cannot send a party duel request without a party.");
                return;
            }

            if (!selectedParty.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You need to select a party to duel first.");
                player.closeInventory();
                event.setCancelled(true);
            }

            if (!ManagerHandler.getPartyManager().getParties().containsKey(selectedParty.get(player.getUniqueId()))) {
                player.sendMessage(ChatColor.RED + "That party is no longer available.");
                player.closeInventory();
                event.setCancelled(true);
            }

            Party targetParty = ManagerHandler.getPartyManager().getParty(selectedParty.get(player.getUniqueId()));

            if (targetParty.getPlayers().size() < 2) {
                player.sendMessage(ChatColor.RED + "That party does not have enough players to join a party duel.");
                player.closeInventory();
                event.setCancelled(true);
            }

            Ladder ladder = ManagerHandler.getLadderManager().getLadders().get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            if (ladder != null) {
                targetParty.addRequest(profile.getParty(), ladder);

                UUID sender = profile.getParty().getIdentifier();
                UUID target = targetParty.getIdentifier();

                new BukkitRunnable() {
                    public void run() {
                        Party rParty = ManagerHandler.getPartyManager().getParty(target);
                        Party sParty = ManagerHandler.getPartyManager().getParty(sender);

                        if (rParty == null || sParty == null) {
                            return;
                        }

                        if (rParty.hasRequest(sParty)) {
                            rParty.removeRequest(sParty);

                            sParty.sendMessage(ChatColor.RED + "Your duel request to " + rParty.getLeader().getName() + "'s party has expired.");
                            rParty.sendMessage(ChatColor.RED + "The duel request sent by " + sParty.getLeader().getName() + "'s party to your party has expired.");
                        }
                    }
                }.runTaskLater(PracticePlugin.getInstance(), 20L * 60);
            }
            else {
                player.sendMessage(ChatColor.RED + "Could not find that ladder.");
            }

            player.closeInventory();
            event.setCancelled(true);
        }
        else if (inv.getTitle().equals(ChatColor.GOLD + "Send Duel Request")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getParty() != null) {
                player.sendMessage(ChatColor.RED + "You cannot send a duel request while in a party.");
                return;
            }

            if (!selectedPlayer.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You need to select a player to duel first.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            Player target = Bukkit.getPlayer(selectedPlayer.get(player.getUniqueId()));

            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player is no longer online.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(target);

            if (targetProfile.hasRequest(player)) {
                player.sendMessage(ChatColor.RED + "You have already sent that player a duel request.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            if (targetProfile.isHidingDuels()) {
                player.sendMessage(ChatColor.RED + "That player is not currently receiving duel requests.");
                player.closeInventory();
                event.setCancelled(true);
                return;
            }

            Ladder ladder = ManagerHandler.getLadderManager().getLadders().get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            if (ladder != null) {
                DuelRequest request = new DuelRequest(player, Bukkit.getPlayer(selectedPlayer.get(player.getUniqueId())), ladder);

                targetProfile.addRequest(player, request);

                new BukkitRunnable() {
                    public void run() {
                        if (Bukkit.getPlayer(target.getUniqueId()) == null) return;
                        if (ManagerHandler.getPlayerManager().getPlayerProfile(target) == null) return;

                        PracticeProfile targetProfile = ManagerHandler.getPlayerManager().getPlayerProfile(target);

                        if (targetProfile.hasRequest(player)) {
                            if (targetProfile.getRequest(player).getIdentifier().equals(request.getIdentifier())) {
                                targetProfile.removeRequest(player);

                                player.sendMessage(ChatColor.RED + "Your duel request to " + targetProfile.getPlayer().getName() + " has expired.");
                                targetProfile.getPlayer().sendMessage(ChatColor.RED + "The duel request sent by " + player.getName() + " to you has expired.");
                            }
                        }
                    }
                }.runTaskLater(PracticePlugin.getInstance(), 20L * 60);
            } else {
                player.sendMessage(ChatColor.RED + "Could not find that ladder.");
            }

            player.closeInventory();
            event.setCancelled(true);
        }
        else if (inv.getTitle().equals(ChatColor.AQUA + "Party Events - Select an event")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (!event.getCurrentItem().getItemMeta().hasDisplayName()) {
                return;
            }

            if (profile.getParty() == null) {
                player.sendMessage(ChatColor.RED + "You must be in a party to open the event menu.");
                player.closeInventory();
                event.setCancelled(true);
            }

            if (!(profile.getParty().getPlayers().size() > 1)) {
                player.sendMessage(ChatColor.RED + "You must have more than 2 players in your party to start an event.");
                player.closeInventory();
                event.setCancelled(true);
            }

            if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Free-for-All")) {
                selectedEvent.put(player.getUniqueId(), PartyEventType.FREE_FOR_ALL);
            }
            else if (event.getCurrentItem().getItemMeta().getDisplayName().contains("Team Deathmatch")) {
                selectedEvent.put(player.getUniqueId(), PartyEventType.TEAM_DEATHMATCH);
            }
            else {
                player.sendMessage(ChatColor.RED + "That party event doesn't exist.");
                player.closeInventory();
                return;
            }

            player.closeInventory();

            Inventory newInv = Bukkit.createInventory(null, ManagerHandler.getLadderManager().getLadderAmount(), ChatColor.AQUA + "Party Events - Select a ladder");

            if (!ManagerHandler.getLadderManager().getLadders().isEmpty()) {
                for (Ladder ladder : ManagerHandler.getLadderManager().getLadders().values()) {
                    newInv.setItem(ladder.getDisplayOrder(), new ItemBuilder(ladder.getDisplayIcon().getType(), MessageUtils.color(ladder.getDisplayName()), 1, ladder.getDisplayIcon().getDurability(), ChatColor.GRAY + "Click to select.").getItem());
                }
            }

            player.openInventory(newInv);
        }
        else if (inv.getTitle().equals(ChatColor.AQUA + "Party Events - Select a ladder")) {
            if (event.getCurrentItem() == null) {
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.AIR)) {
                return;
            }

            if (profile.getParty() == null) {
                player.sendMessage(ChatColor.RED + "You must be in a party to open the event menu.");
                player.closeInventory();
                event.setCancelled(true);
            }

            if (!(profile.getParty().getPlayers().size() > 1)) {
                player.sendMessage(ChatColor.RED + "You must have more than 2 players in your party to start an event.");
                player.closeInventory();
                event.setCancelled(true);
            }

            Ladder ladder = ManagerHandler.getLadderManager().getLadders().get(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));

            if (ladder == null) {
                player.sendMessage(ChatColor.RED + "Could not find that ladder.");
                return;
            }

            if (!selectedEvent.containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "You did not select an event in the previous menu.");
                return;
            }

            switch(selectedEvent.get(player.getUniqueId())) {
                case TEAM_DEATHMATCH:
                    List<Player> team1 = new ArrayList<>();
                    List<Player> team2 = new ArrayList<>();

                    for (Player p : profile.getParty().getPlayers()) {
                        if (team1.size() > team2.size()) {
                            team2.add(p);
                        } else if (team2.size() > team1.size()) {
                            team1.add(p);
                        } else {
                            Random r = new Random();

                            if (r.nextBoolean()) {
                                team1.add(p);
                            } else {
                                team2.add(p);
                            }
                        }
                    }

                    TeamMatch teamMatch = new TeamMatch(null, ladder, ManagerHandler.getArenaManager().getRandomArena(), MatchType.PARTY_VERSUS_PARTY, team1, team2);
                    ManagerHandler.getMatchManager().getMatches().put(teamMatch.getIdentifier(), teamMatch);
                    break;
                case FREE_FOR_ALL:
                    FfaMatch ffaMatch = new FfaMatch(ladder, ManagerHandler.getArenaManager().getRandomArena(), profile.getParty().getPlayers());
                    ManagerHandler.getMatchManager().getMatches().put(ffaMatch.getIdentifier(), ffaMatch);
                    break;
                default:
                    player.sendMessage(ChatColor.RED + "That party event doesn't exist.");
            }

            player.closeInventory();
            event.setCancelled(true);
        }
    }

}