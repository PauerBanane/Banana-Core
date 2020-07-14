package de.pauerbanane.core.addons.plotshop.dynmap;

import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotManager;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.AreaMarker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerSet;

import java.util.Collection;

public class DynmapManager {

    private static final String key = "plots";

    private DynmapCommonAPI dynmapAPI;

    private MarkerAPI markerAPI;

    private PlotShop addon;

    private PlotManager plotManager;

    private FileLoader config;

    private boolean enabled;

    public DynmapManager(PlotShop addon) {
        this.dynmapAPI = BananaCore.getDynmapAPI();
        this.markerAPI = dynmapAPI.getMarkerAPI();
        this.addon = addon;
        this.plotManager = addon.getManager();

        initConfig();
    }

    private void initConfig() {
        this.config = new FileLoader(addon.getAddonFolder(), "Dynmap.yml");
        if(!config.isSet(key + ".enabled")) {
            config.set(key + ".enabled", false).save();
        }

        ConfigurationSection section = config.getConfigurationSection(key);
        enabled = section.getBoolean("enabled");
    }

    public void loadPlots() {
        if(!enabled)
            return;

        Collection<Plot> plots = plotManager.getPlotMap();

        MarkerSet markerSet = markerAPI.getMarkerSet(key);
        if(markerSet != null)
            markerSet.deleteMarkerSet();

        markerSet = markerAPI.createMarkerSet(key, key, markerAPI.getMarkerIcons(), false);

        for (Plot plot : plots) {
            String markerID = plot.getRegionID();
            double[] x = {plot.getRegion().getMaximumPoint().getBlockX(), plot.getRegion().getMinimumPoint().getBlockX()};
            double[] z = {plot.getRegion().getMaximumPoint().getBlockZ(), plot.getRegion().getMinimumPoint().getBlockZ()};
            AreaMarker areaMarker = markerSet.createAreaMarker("plot." + plot.getWorld() + "." + markerID, markerID, false, plot.getWorld(), x, z, false);
            String owner = plot.getOwner() == null ? "frei" : Bukkit.getOfflinePlayer(plot.getOwner()).getName();
            areaMarker.setDescription("Owner: " + owner + "\nPlot: " + plot.getRegionID() + "\nPreis: " + plot.getPrice());
        }
    }



}
