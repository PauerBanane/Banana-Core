package de.pauerbanane.core.addons.plotshop.events;

import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlotEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Plot plot;

    public Plot getPlot() {
        return this.plot;
    }

    public PlotEvent(Plot plot) {
        this.plot = plot;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}