package de.pauerbanane.core.addons.discovery.events;

import de.pauerbanane.core.addons.discovery.Discovery;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import scala.concurrent.impl.FutureConvertersImpl;

public class DiscoveryUnlockEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Discovery discovery;

    private Player player;

    public DiscoveryUnlockEvent(Discovery discovery, Player player) {
        this.discovery = discovery;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public Discovery getDiscovery() {
        return discovery;
    }

    public Player getPlayer() {
        return player;
    }
}
