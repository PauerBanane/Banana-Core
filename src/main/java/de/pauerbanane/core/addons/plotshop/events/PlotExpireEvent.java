package de.pauerbanane.core.addons.plotshop.events;

import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.event.HandlerList;

public class PlotExpireEvent extends PlotEvent {

    public PlotExpireEvent(Plot plot) {
        super(plot);
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}