package de.pauerbanane.core.addons.playershop.events;

import de.pauerbanane.core.addons.playershop.Shop;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerShopRentEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Shop shop;

    public PlayerShopRentEvent(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return this.shop;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}