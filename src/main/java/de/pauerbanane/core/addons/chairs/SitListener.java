package de.pauerbanane.core.addons.chairs;

import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.settings.data.Settings;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SitListener implements Listener {

    private Chairs addon;

    public SitListener(Chairs addon) {
        this.addon = addon;
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(e.getPlayer().getInventory().getItemInMainHand() != null && e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;
        if(!(e.getClickedBlock().getBlockData() instanceof Stairs)) return;
        if(e.getPlayer().isSneaking()) return;
        if(((Stairs) e.getClickedBlock().getBlockData()).getHalf() == Bisected.Half.TOP) return;
        Location above = e.getClickedBlock().getLocation().clone().add(0,1,0);
        if(above.getBlock() == null || !above.getBlock().isPassable()) return;

        CorePlayer cp = CorePlayer.get(e.getPlayer().getUniqueId());
        Settings settings = cp.getData(Settings.class);

        if(!settings.autoSitEnabled()) return;
        addon.toggleSit(e.getPlayer(), e.getClickedBlock(), null);
    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        if(addon.isSitting(e.getPlayer()))
            addon.toggleSit(e.getPlayer(), null, e.getCause());
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent e) {
        if(addon.isSitting(e.getPlayer()))
            addon.toggleSit(e.getPlayer(), null, null);
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent e) {
        if(addon.isSitting(e.getEntity()))
            addon.toggleSit(e.getEntity(), null, null);
    }

    @EventHandler
    public void handleManipulation(PlayerArmorStandManipulateEvent e) {
        if(addon.isSeat(e.getRightClicked())) {
            e.getPlayer().sendMessage(F.error("Sit", "Du kannst den ArmorStand nicht bearbeiten, während jemand darauf sitzt."));
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBlockBreak(BlockBreakEvent e) {
        if(addon.isOccupied(e.getBlock())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(F.error("Sit", "Du kannst diesen Block nicht abbauen, da jemand darauf sitzt."));
        }
    }

}
