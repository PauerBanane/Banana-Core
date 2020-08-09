package de.pauerbanane.core.addons.regrowingTrees.listener;

import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.core.addons.regrowingTrees.RegrowingTrees;
import de.pauerbanane.core.addons.regrowingTrees.Tree;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class RegrowingTreesListener implements Listener {

    private RegrowingTrees addon;

    public RegrowingTreesListener(RegrowingTrees addon) {
        this.addon = addon;
    }

    @EventHandler
    public void handleTreeChopping(BlockBreakEvent e) {
        if (e.isCancelled()) return;
        Tree tree = addon.getManager().treeChopped(e.getBlock());
        if (tree == null) return;

        e.getBlock().getWorld().playEffect(e.getBlock().getLocation(), Effect.SMOKE, 1);
        e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.ENTITY_BLAZE_SHOOT, 1, 1);
        tree.startCountdown();
    }

}
