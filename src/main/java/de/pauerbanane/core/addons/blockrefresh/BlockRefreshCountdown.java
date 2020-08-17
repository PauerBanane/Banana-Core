package de.pauerbanane.core.addons.blockrefresh;

import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitTask;

public class BlockRefreshCountdown {

    private BukkitTask task;

    private Block block;

    private RefreshChain chain;

    private boolean isRunning;

    public BlockRefreshCountdown(Block block, RefreshChain chain, Material current) {
        this.block = block;
        this.chain = chain;

        Bukkit.getScheduler().runTaskLater(BananaCore.getInstance(), () -> start(current), 1);
    }

    public void start(Material current) {
        if (isRunning) return;
        isRunning = true;

        block.setType(chain.getNext(current));

        task = Bukkit.getScheduler().runTaskLater(BananaCore.getInstance(), () -> {
            block.setType(chain.getFirst());
            stop();
            BlockRefreshAddon.getInstance().removeCountdown(block);
        }, 20 * 5);
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;
        task.cancel();
    }

    public void restart(Material current) {
        task.cancel();
        isRunning = false;

        Bukkit.getScheduler().runTaskLater(BananaCore.getInstance(), () -> start(current), 1);
    }

    public void forceFinish() {
        if (!isRunning) return;
        stop();
        block.setType(chain.getFirst());
    }

    public boolean isRunning() {
        return isRunning;
    }

    public RefreshChain getChain() {
        return chain;
    }

    public Block getBlock() {
        return block;
    }
}
