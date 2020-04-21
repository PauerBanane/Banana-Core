package de.pauerbanane.core.addons.plotshop;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.pauerbanane.api.util.F;
import org.bukkit.Bukkit;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlotPlayer {

    private UUID player;

    private HashMap<String, Plot> ownedPlots;

    private int plots;

    public PlotPlayer(UUID uuid) {
        this.ownedPlots = Maps.newHashMap();
        this.player = uuid;
    }

    public void addPlot(Plot plot) {
        if(plot.getOwner() != null && plot.getOwner().equals(player))
            ownedPlots.put(plot.getRegionID(), plot);
        plots = ownedPlots.size();
    }

    public void removePlot(Plot plot) {
        if(Bukkit.getPlayer(player) != null)
            Bukkit.getPlayer(player).sendMessage(F.main("Plot", "Dein Plot §e" + plot.getRegionID() + " §7in der Welt §e" + plot.getWorld() + " §7wurde gelöscht"));
        ownedPlots.remove(plot.getRegionID());
        plots = this.ownedPlots.size();
    }

    public Collection<Plot> getPlots(PlotGroup group) {
        if(ownedPlots.isEmpty())
            return Sets.newHashSetWithExpectedSize(0);
        return ownedPlots.values().stream().filter(plot -> plot.getPlotGroup().getGroupID().equals(group)).collect(Collectors.toSet());
    }

    public Collection<Plot> getPlots() {
        return ownedPlots.values();
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isOnline() {
        return (Bukkit.getPlayer(this.player) != null);
    }

    public int getPlotAmount(PlotGroup plotGroup) {
        if(ownedPlots.isEmpty())
            return 0;
        return (int) ownedPlots.values().stream().filter(plot -> plot.getPlotGroup().getGroupID().equals(plotGroup.getGroupID())).count();
    }
}
