package de.pauerbanane.core.addons.afk;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.afk.commands.AFKCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AFK extends Addon implements Runnable, Listener {

    private long afkAfter;
    private long kickAfter;
    private HashMap<UUID, Long> afkTime;
    private HashMap<Player, Footprint> lastMovePosition;

    @Override
    public void onEnable() {
        this.afkTime = new HashMap<>();
        this.lastMovePosition = new HashMap<>();
        this.afkAfter  = TimeUnit.MINUTES.toMillis(5);
        this.kickAfter = TimeUnit.MINUTES.toMillis(10);

        registerCommand(new AFKCommand(this));
        registerListener(this);

        for (Player player : Bukkit.getOnlinePlayers())
            this.lastMovePosition.put(player, new Footprint(player.getLocation().getYaw(), System.currentTimeMillis()));
        Bukkit.getScheduler().runTaskTimer(BananaCore.getInstance(), this, 100L, 100L);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    public void setAfk(Player player, boolean status) {
        if(status) {
            this.afkTime.put(player.getUniqueId(), Long.valueOf(System.currentTimeMillis()));
        } else {
            lastMovePosition.put(player, new Footprint(player.getLocation().getYaw(), System.currentTimeMillis()));
            afkTime.remove(player.getUniqueId());
        }
    }

    public void updateAFK(Player player) {
        if(player.hasPermission("afk.bypass"))
            return;
        Footprint footprint = lastMovePosition.get(player);
        if (player.getLocation().getYaw() != footprint.getYaw()) {
            this.lastMovePosition.put(player, new Footprint(player.getLocation().getYaw(), System.currentTimeMillis()));
            if (isAfk(player)) {
                Bukkit.broadcastMessage(F.main("AFK", "§e" + player.getName() + " §7ist nach " + ChatColor.LIGHT_PURPLE + UtilTime.getElapsedTime(getAfkTime().get(player.getUniqueId())) + " §7wieder da."));
                setAfk(player, false);
                return;
            }
        }
        if (!isAfk(player) && UtilTime.isElapsed(footprint.getTime() + afkAfter)) {
            Bukkit.broadcastMessage(F.main("AFK", "§e" + player.getName() + " §7ist nun AFK."));
            setAfk(player, true);
            return;
        }
        if (isAfk(player) && UtilTime.isElapsed(footprint.getTime() + this.kickAfter)) {
            player.kickPlayer("Du warst zu lange AFK.");
            return;
        }
    }

    // Events

    @EventHandler
    public void loadCache(PlayerJoinEvent event) {
        this.lastMovePosition.put(event.getPlayer(), new Footprint(event.getPlayer().getLocation().getYaw(), System.currentTimeMillis()));
    }

    @EventHandler
    public void unloadCache(PlayerQuitEvent event) {
        this.lastMovePosition.remove(event.getPlayer());
        this.afkTime.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {
        updateAFK(event.getPlayer());
    }

    @EventHandler
    public void blockFish(PlayerFishEvent event) {
        if (isAfk(event.getPlayer()) && event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1.0F, 0.85F);
            event.getPlayer().sendActionBar("§cDu kannst im AFK Modus nicht mit der Welt interagieren.");
            event.setCancelled(true);
            event.setExpToDrop(0);
        }
    }

    @EventHandler
    public void blockInteract(PlayerInteractEvent event) {
        if (isAfk(event.getPlayer())) {
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1.0F, 0.85F);
            event.getPlayer().sendActionBar("§cDu kannst im AFK Modus nicht mit der Welt interagieren.");
            event.setCancelled(true);
        }
    }



    public boolean isAfk(Player player) {
        return this.afkTime.containsKey(player.getUniqueId());
    }
    public long getAFKTime(Player player) {
        return ((Long)this.afkTime.getOrDefault(player, Long.valueOf(0L))).longValue();
    }

    public HashMap<UUID, Long> getAfkTime() {
        return afkTime;
    }

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers())
            updateAFK(player);
    }
}
