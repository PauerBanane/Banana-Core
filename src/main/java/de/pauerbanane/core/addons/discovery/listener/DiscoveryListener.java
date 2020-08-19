package de.pauerbanane.core.addons.discovery.listener;

import com.destroystokyo.paper.Title;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.discovery.Discovery;
import de.pauerbanane.core.addons.discovery.DiscoveryAddon;
import de.pauerbanane.core.addons.discovery.data.DiscoveryData;
import de.pauerbanane.core.addons.discovery.events.DiscoveryUnlockEvent;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DiscoveryListener implements Listener {

    private DiscoveryAddon addon;

    public DiscoveryListener(DiscoveryAddon addon) {
        this.addon = addon;
    }

    @EventHandler
    public void handleRegionEnter(RegionEnterEvent e) {
        Discovery discovery = addon.getDiscovery(e.getPlayer().getWorld().getName(), e.getRegion().getId());
        if (discovery == null) return;

        DiscoveryData data = CorePlayer.get(e.getPlayer().getUniqueId()).getData(DiscoveryData.class);
        if (data.hasAchievedDiscovery(discovery)) return;

        new DiscoveryUnlockEvent(discovery, e.getPlayer()).callEvent();
    }

    @EventHandler
    public void handleDiscoveryUnlock(DiscoveryUnlockEvent e) {
        Bukkit.broadcastMessage(F.main("Discovery", "§6" + e.getPlayer().getName() + " §7hat §a" + e.getDiscovery().getName() + " §7entdeckt!"));
        UtilPlayer.playSound(e.getPlayer(), Sound.UI_TOAST_CHALLENGE_COMPLETE);

        sendTitle(e.getPlayer(), e.getDiscovery());

        DiscoveryData data = CorePlayer.get(e.getPlayer().getUniqueId()).getData(DiscoveryData.class);
        data.addDiscovery(e.getDiscovery());
    }

    private void sendTitle(Player player, Discovery discovery) {
        player.sendTitle(new Title("§a" + discovery.getName() + " §eentdeckt", "",0,100,0));
    }

}
