package de.pauerbanane.core.addons.carriages.countdowns;

import com.destroystokyo.paper.Title;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.carriages.Carriage;
import de.pauerbanane.core.addons.carriages.gui.CarriageLineGUI;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CarriageTeleportCountdown {

    private int taskID,
                taskID2;

    private boolean isRunning;

    private Player player;

    private Carriage carriage;

    private int seconds;

    public CarriageTeleportCountdown(Player player, Carriage carriage) {
        this.player = player;
        this.carriage = carriage;
        this.seconds = 6;

        start();
    }

    private void start() {
        if(isRunning) return;
        isRunning = true;

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {
            switch (seconds) {
                default:
                    sendSubTitle();
                    break;
                case 6:
                    UtilPlayer.playSound(player, Sound.ENTITY_HORSE_ANGRY);
                    sendTitle();
                    break;
                case 5:
                    sendSubTitle();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 200, 4));
                    break;
                case 2:
                    sendSubTitle();
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
                    break;
                case 0:
                    player.teleport(carriage.getTargetLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    UtilPlayer.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN);
                    stop();
                    break;
            }

            seconds--;
        },0, 20);

        taskID2 = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {
            UtilPlayer.playSound(player, Sound.ENTITY_HORSE_GALLOP);
        }, 0, 10);

    }

    public void stop() {
        if(!isRunning) return;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
        Bukkit.getScheduler().cancelTask(taskID2);
        CarriageLineGUI.runningCountdowns.remove(player.getUniqueId());
    }

    private void sendTitle() {
        player.sendTitle(new Title("§7Reise nach §e" + carriage.getName(), "",0,20,0));
    }

    private void sendSubTitle() {
        player.sendTitle(new Title("", "§8Teleport in " + seconds + " Sekunden...", 0,20,0));
    }

}
