package de.pauerbanane.core.addons.blockrefresh.listener;

import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.core.addons.blockrefresh.BlockRefreshAddon;
import de.pauerbanane.core.data.FlagManager;
import de.pauerbanane.core.util.UtilWorldGuard;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockRefreshListener implements Listener {

    private BlockRefreshAddon addon;

    public BlockRefreshListener(BlockRefreshAddon addon) {
        this.addon = addon;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        ProtectedRegion region = UtilWorldGuard.getRegion(e.getBlock());
        StateFlag blockRefreshFlag = FlagManager.getInstance().getBlockrefreshFlag();
        if (region == null || blockRefreshFlag == null || region.getFlag(blockRefreshFlag) != StateFlag.State.ALLOW) return;
        if (!addon.isInChain(e.getBlock().getType()) && !addon.isInCountdown(e.getBlock())) return;
        addon.addCountdown(e.getBlock(), e.getBlock().getType());
    }

}
