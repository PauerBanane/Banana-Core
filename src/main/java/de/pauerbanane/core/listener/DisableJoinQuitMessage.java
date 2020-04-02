package de.pauerbanane.core.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DisableJoinQuitMessage implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void disableJoinMessage(PlayerJoinEvent e) {
        e.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void disableQuitMessage(PlayerQuitEvent e) {
        e.setQuitMessage(null);
    }

}
