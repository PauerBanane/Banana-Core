package de.pauerbanane.core.data;

import com.google.common.collect.Maps;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.regionevents.RegionEnteredEvent;
import de.pauerbanane.api.regionevents.RegionLeaveEvent;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.util.UtilWorldGuard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class RegionManager implements Listener {

    private static RegionManager instance;

    private BananaCore plugin;

    private HashMap<UUID, ProtectedRegion> regionCache;

    public RegionManager(BananaCore plugin) {
        instance = this;
        this.plugin = plugin;
        this.regionCache = Maps.newHashMap();

        plugin.registerListener(this);
    }

    public HashMap<UUID, ProtectedRegion> getRegionCache() {
        return regionCache;
    }

    @EventHandler
    public void handleRegionEnter(RegionEnterEvent e) {
        regionCache.put(e.getPlayer().getUniqueId(), e.getRegion());
    }
    @EventHandler
    public void handleRegionLeave(RegionLeaveEvent e) {
       regionCache.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent e) {
        regionCache.remove(e.getPlayer().getUniqueId());
    }

    public ProtectedRegion getRegion(Player player) {
        if (regionCache.containsKey(player.getUniqueId())) {
            return regionCache.get(player.getUniqueId());
        } else {
            return UtilWorldGuard.getRegion("__global__", player.getWorld());
        }
    }

    public String getCurrentRegionName(Player player) {
        ProtectedRegion region = getRegion(player);
        String name = region.getFlag(FlagManager.getInstance().getRegionNameFlag());
        return name != null ? name : FlagManager.getInstance().getRegionNameFlag().getDefault();
    }

    public static RegionManager getInstance() {
        return instance;
    }
}
