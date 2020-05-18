package de.pauerbanane.core.addons.plotshop.scoreboard;

import de.pauerbanane.api.scoreboards.api.Entry;
import de.pauerbanane.api.scoreboards.api.EntryBuilder;
import de.pauerbanane.api.scoreboards.api.ScoreboardHandler;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.entity.Player;

import java.util.List;

public class PlotBoard implements ScoreboardHandler {

    private Plot plot;

    public PlotBoard(Plot plot) {
        this.plot = plot;
    }

    @Override
    public String getTitle(Player player) {
        return "Plot: " + this.plot.getRegionID();
    }

    @Override
    public List<Entry> getEntries(Player player) {

        return (new EntryBuilder()).blank()
                .next("§fPreis: §e"  + this.plot.getPrice())
                .next("§fGruppe: §e" + this.plot.getPlotGroup().getGroupID())
                .next("§fMietbar: §e" + F.tf(plot.isRentable()))
                .blank()
                .next("§7Tippe §e/plot kaufen")
                .next("§7um dieses Grundstück zu")
                .next("§7kaufen.")
                .build();
    }
}
