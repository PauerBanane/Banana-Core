package de.pauerbanane.core.addons.plotshop.events;

import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.event.HandlerList;

public class PlotPurchaseEvent extends PlotEvent {

    public PlotPurchaseEvent(Plot plot) {
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
