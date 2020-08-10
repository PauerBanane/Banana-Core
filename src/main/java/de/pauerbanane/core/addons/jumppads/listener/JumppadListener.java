package de.pauerbanane.core.addons.jumppads.listener;

import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.jumppads.Jumppad;
import de.pauerbanane.core.addons.jumppads.JumppadManager;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JumppadListener implements Listener {

    private JumppadManager manager;

    public JumppadListener(JumppadManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onRegionEnter(RegionEnterEvent e) {
        if (!manager.isJumppadRegion(e.getPlayer().getWorld(), e.getRegion())) return;
        if (e.getPlayer().isSneaking() && e.getPlayer().hasPermission("command.jumppad")) return;
        Jumppad jumppad = manager.getJumppad(e.getPlayer().getWorld(), e.getRegion().getId());

        jumppad.useJumppad(e.getPlayer());
    }

}
