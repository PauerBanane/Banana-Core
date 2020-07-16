package de.pauerbanane.core.addons.carriages.listener;

import com.google.common.collect.Maps;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.regionevents.RegionLeaveEvent;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.core.addons.carriages.Carriage;
import de.pauerbanane.core.addons.carriages.CarriageManager;
import de.pauerbanane.core.addons.carriages.events.CarriageEnterEvent;
import de.pauerbanane.core.addons.chairs.events.SitEvent;
import de.pauerbanane.core.addons.carriages.gui.CarriageLineGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class CarriageListener implements Listener {


    private CarriageManager manager;

    private HashMap<UUID, ProtectedRegion> regionCache;

    public CarriageListener(CarriageManager manager) {
        this.manager = manager;
        this.regionCache = Maps.newHashMap();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRegionEnter(RegionEnterEvent e) {
        if(!manager.isCarriageRegion(e.getRegion().getId(), e.getPlayer().getWorld().getName())) return;
        regionCache.put(e.getPlayer().getUniqueId(), e.getRegion());
    }

    @EventHandler
    public void handleRegionLeave(RegionLeaveEvent e) {
        if (!regionCache.containsKey(e.getPlayer().getUniqueId()) || regionCache.get(e.getPlayer().getUniqueId()) != e.getRegion()) return;

        regionCache.remove(e.getPlayer().getUniqueId());

        if (CarriageLineGUI.runningCountdowns.containsKey(e.getPlayer().getUniqueId()))
            CarriageLineGUI.runningCountdowns.get(e.getPlayer().getUniqueId()).stop();

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSit(SitEvent e) {
        if(!regionCache.containsKey(e.getPlayer().getUniqueId())) return;
        Carriage carriage = manager.getCarriageByRegion(regionCache.get(e.getPlayer().getUniqueId()).getId(), e.getPlayer().getWorld().getName());
        if (carriage == null) return;

        new CarriageEnterEvent(e.getPlayer(), e.getChair(), carriage).callEvent();

    }

    @EventHandler
    public void onCarriageEnter(CarriageEnterEvent e) {
        if (e.isCancelled()) return;
        SmartInventory.builder().provider(new CarriageLineGUI(e.getCarriageLine())).title("§7§lReiseziel auswählen: §e" + e.getCarriage().getName()).size(3).build().open(e.getPlayer());
    }

}
