package de.pauerbanane.core.addons.skyblock.listener;

import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.scoreboard.PurchasedPlotBoard;
import de.pauerbanane.core.addons.skyblock.SkyblockAddon;
import de.pauerbanane.core.addons.skyblock.scoreboard.SkyblockBoard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class SkyblockListener implements Listener {

    private SkyblockAddon addon;

    public SkyblockListener(SkyblockAddon addon) {
        this.addon = addon;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        BananaCore.getScoreboardAPI().getBoardManager().enableScoreboard(e.getPlayer());
        BananaCore.getScoreboardAPI().getBoardManager().setBoard(e.getPlayer(), new SkyblockBoard());
    }

}
