package de.pauerbanane.core.addons.plotshop.events;

import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class PlotRentEvent extends PlotEvent {

    private final Player newOwner;

    public Player getNewOwner() {
        return this.newOwner;
    }

    public PlotRentEvent(Plot plot, Player newOwner) {
        super(plot);
        this.newOwner = newOwner;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}