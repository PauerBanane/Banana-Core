package de.pauerbanane.core.addons.portals.listener;

import com.google.common.collect.Maps;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.regionevents.RegionLeaveEvent;
import de.pauerbanane.core.addons.portals.PortalCountdown;
import de.pauerbanane.core.addons.portals.Portals;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

public class PortalEnterListener implements Listener {

    public static HashMap<UUID, PortalCountdown> runningCountdowns;

    private Portals addon;

    public PortalEnterListener(Portals addon) {
        this.addon = addon;
        this.runningCountdowns = Maps.newHashMap();
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {
        if(!addon.isPortalRegion(e.getPlayer().getWorld(), e.getRegion().getId())) return;
        runningCountdowns.put(e.getPlayer().getUniqueId(), new PortalCountdown(e.getPlayer(), addon.getPortalByRegion(e.getPlayer().getWorld(), e.getRegion().getId())));
    }

    @EventHandler
    public void onLeave(RegionLeaveEvent e) {
        if(!runningCountdowns.containsKey(e.getPlayer().getUniqueId())) return;
        runningCountdowns.get(e.getPlayer().getUniqueId()).stop();
    }

}
