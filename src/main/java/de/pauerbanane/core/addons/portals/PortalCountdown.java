package de.pauerbanane.core.addons.portals;

import com.destroystokyo.paper.Title;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.portals.listener.PortalEnterListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PortalCountdown {

    private int taskID;

    private boolean isRunning;

    private Player player;

    private Portal portal;

    private int seconds;

    public PortalCountdown(Player player, Portal portal) {
        this.player = player;
        this.portal = portal;
        this.seconds = 6;

        start();
    }

    private void start() {
        if(isRunning) return;
        isRunning = true;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {

            switch (seconds) {
                case 6:
                    sendTitle();
                    break;
                case 5: case 3:
                    sendSubTitle();
                    break;
                case 4:
                    sendSubTitle();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 2));
                    break;
                case 2:
                    sendSubTitle();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 4));
                    break;
                case 1:
                    sendSubTitle();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 4));
                    break;
                case 0:
                    portal.teleport(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 4));
                    stop();
                    break;
            }

            seconds--;
        }, 0, 20);
    }

    public void stop() {
        if(!isRunning) return;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
        PortalEnterListener.runningCountdowns.remove(player);
    }

    private void sendTitle() {
        player.sendTitle(new Title("§7Reise nach " + portal.getDescription(), "",0,20,0));
    }

    private void sendSubTitle() {
        player.sendTitle(new Title("", "§8Teleport in " + seconds + " Sekunden...", 0,20,0));
    }

}
