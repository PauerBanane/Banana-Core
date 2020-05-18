package de.pauerbanane.core.addons.beds;

import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.Addon;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Beds extends Addon implements Listener {

    private int percentage = 51;

    @Override
    public void onEnable() {
        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer().isSleepingIgnored())
            event.getPlayer().setSleepingIgnored(false);
    }

    @EventHandler
    public void onBedLeave(PlayerBedLeaveEvent event) {
        if (event.getPlayer().isSleepingIgnored())
            event.getPlayer().setSleepingIgnored(false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBedEnter(PlayerBedEnterEvent event) {
        World world = event.getPlayer().getWorld();
        if (world.getPlayerCount() == 1)
            return;

        int players = world.getPlayerCount();
        int required = Math.round((51 * players / 100));
        int sleeping = 0;
        for (Player player : world.getPlayers()) {
            if (player.isSleeping() || player.isSleepingIgnored())
                sleeping++;
        }
        double res = Math.round(((sleeping * 100) / players) * 100.0D) / 100.0D;
        int missing = required - sleeping;
        Bukkit.getLogger().info("DEBUG: World: " + world.getName() + " Sleeping: " + sleeping + " Total: " + players + " Required: " + required + " percentage: " + res);
        if (missing > 0) {
            for (Player player : event.getPlayer().getWorld().getPlayers())
                player.sendMessage(F.main("Info", String.valueOf(F.name(event.getPlayer().getName())) + " möchte schlafen. Es fehlen " + F.elem(String.valueOf(required - sleeping)) + " Spieler um die Nacht zu überspringen."));
                        Bukkit.getLogger().info(String.valueOf(F.name(event.getPlayer().getName())) + " möchte schlafen. Es fehlen " + F.elem(String.valueOf(sleeping - required)) + " Spieler um die Nacht zu überspringen.");
        }
        if (res >= 51.0D) {
            System.out.println("More than 51% are sleeping - Attempting to skip the night.");
            for (Player player : world.getPlayers()) {
                if (!player.isSleeping() && !player.isSleepingIgnored()) {
                    player.setSleepingIgnored(true);
                    System.out.println("Set " + player.getName() + " to sleepingIgnored");
                }
            }
        }
    }

}
