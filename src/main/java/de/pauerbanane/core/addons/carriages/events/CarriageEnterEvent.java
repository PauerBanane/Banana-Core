package de.pauerbanane.core.addons.carriages.events;

import de.pauerbanane.core.addons.carriages.Carriage;
import de.pauerbanane.core.addons.carriages.CarriageLine;
import de.pauerbanane.core.addons.chairs.Chair;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CarriageEnterEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private Player player;

    private Chair chair;

    private String regionID;

    private CarriageLine carriageLine;

    private Carriage carriage;

    private boolean cancelled = false;

    public CarriageEnterEvent(Player player, Chair chair, Carriage carriage) {
        this.chair = chair;
        this.player = player;
        this.regionID = carriage.getRegionID();
        this.carriage = carriage;
        this.carriageLine = carriage.getCarriageLine();
    }

    public Chair getChair() {
        return chair;
    }

    public String getRegionID() {
        return regionID;
    }

    public Player getPlayer() {
        return player;
    }


    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    public CarriageLine getCarriageLine() {
        return carriageLine;
    }

    public Carriage getCarriage() {
        return carriage;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
