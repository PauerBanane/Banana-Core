package de.pauerbanane.core.addons.chairs.events;

import de.pauerbanane.core.addons.chairs.Chair;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SitEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private Chair chair;

    private String message;

    private boolean cancelled = false;

    public SitEvent(Player player, Chair chair, String message) {
        this.player = player;
        this.chair = chair;
        this.message = message;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ArmorStand getSeat() {
        return this.chair.getArmorStand();
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Chair getChair() {
        return chair;
    }
}
