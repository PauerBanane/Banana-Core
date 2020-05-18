package de.pauerbanane.core.addons.plotshop;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.WorldEditUtil;
import de.pauerbanane.core.addons.plotshop.events.PlotExpireEvent;
import de.pauerbanane.core.addons.plotshop.events.PlotPurchaseEvent;
import de.pauerbanane.core.addons.plotshop.events.PlotRentEvent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class PlotManager {

    private final PlotShop addon;

    private HashMap<String, PlotGroup> groups;

    private HashMap<String, Plot> plots;

    private HashMap<UUID, PlotPlayer> playerdata;

    private File plotFolder;

    private File schematicFolder;

    private FileLoader groupConfig;

    public PlotManager(PlotShop addon) {
        this.addon = addon;
        this.groups = Maps.newHashMap();
        this.plots = Maps.newHashMap();
        this.playerdata = Maps.newHashMap();
        this.plotFolder = new File(addon.getPlotFolder());
        this.schematicFolder = new File(addon.getSchematicFolder());
        this.groupConfig = addon.getPlotGroupConfig();
    }

    public void load() {
        int groups = 0;
        for(String group : groupConfig.getKeys(false)) {
            ConfigurationSection section = groupConfig.getConfigurationSection(group);
            PlotGroup plotGroup = new PlotGroup(section.getString("id"), section.getInt("limit"));
            plotGroup.setAutoReset(section.getBoolean("autoreset"));
            plotGroup.setRentDays(section.getInt("rentDays"));
            this.groups.put(plotGroup.getGroupID(), plotGroup);
            groups++;
        }

        addon.getPlugin().getLogger().info("Loaded " + groups + " Plotgroups.");
        int plotAmount = 0;
        Iterator<File> plots = FileUtils.iterateFiles(this.plotFolder, new String[] {"yml"}, false);
        while (plots.hasNext()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(plots.next());
            Plot plot = config.getSerializable("plot", Plot.class);
            this.plots.put(plot.getRegionID(), plot);
            if(plot.getOwner() != null)
                getPlayerData(plot.getOwner()).addPlot(plot);
            plotAmount++;
        }
        addon.getPlugin().getLogger().info("Loaded " + plotAmount + " plots.");
    }

    public Collection<PlotGroup> getPlotGroups() {
        return this.groups.values();
    }

    public void purchasePlot(Player player, Plot plot) {
        plot.setOwner(player.getUniqueId());
        plot.setPurchaseDate(LocalDateTime.now());
        plot.getRegion().getOwners().addPlayer(player.getUniqueId());
        if (plot.isRentable()) {
            plot.setExpireDate(LocalDateTime.now().plusDays(plot.getPlotGroup().getRentDays()));
            player.sendMessage(F.main("Plot", "Du hast das Grundstück bis zum §e" + plot.getExpireDateFormatted() + " §7gemietet."));
            new PlotRentEvent(plot, player).callEvent();
        } else {
            new PlotPurchaseEvent(plot).callEvent();
        }
        getPlayerData(player.getUniqueId()).addPlot(plot);
        savePlot(plot);
    }

    public void reRentPlot(Player player, Plot plot) {
        if(plot.isRentable()) {
            plot.setExpireDate(LocalDateTime.now().plusDays(plot.getPlotGroup().getRentDays()));
            player.sendMessage(F.main("Plot", "Du hast das Grundstück bis zum §e" + plot.getExpireDateFormatted() + " §7verlängert."));
            savePlot(plot);
        }
    }

    public void savePlot(Plot plot) {
        File file = new File(this.plotFolder, plot.getRegionID() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("plot", plot);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deletePlot(Plot plot) {
        this.plots.remove(plot.getRegionID());
        if (plot.getOwner() != null)
            (playerdata.get(plot.getOwner())).removePlot(plot);
        File file = new File(plotFolder, plot.getRegionID() + ".yml");
        file.delete();
        File schematic = new File(schematicFolder, String.valueOf(plot.getRegionID()) + ".schem");
        schematic.delete();
        plot.getRegion().getOwners().removeAll();
        plot.getRegion().getMembers().removeAll();
        if (plot.isRentable())
            new PlotExpireEvent(plot).callEvent();
    }

    public void registerPlot(Plot plot) throws IOException {
        CuboidRegion cuboidRegion = new CuboidRegion(plot.getRegion().getMinimumPoint(), plot.getRegion().getMaximumPoint());
        WorldEditUtil.saveRegionBlocks(new File(this.schematicFolder, plot.getRegionID() + ".schem"), (Region)cuboidRegion, Bukkit.getWorld(plot.getWorld()));
        this.plots.put(plot.getRegionID(), plot);
        savePlot(plot);
    }

    public void resetPlot(Plot plot) throws MaxChangedBlocksException, IOException {
        getPlayerData(plot.getOwner()).removePlot(plot);
        plot.setOwner(null);
        plot.setPurchaseDate(null);
        plot.getRegion().getMembers().removeAll();
        plot.getRegion().getOwners().removeAll();
        savePlot(plot);
        CuboidRegion cuboidRegion = new CuboidRegion(plot.getRegion().getMinimumPoint(), plot.getRegion().getMaximumPoint());
        WorldEditUtil.restoreRegionBlocks(new File(schematicFolder, plot.getRegionID() + ".schem"), cuboidRegion, Bukkit.getWorld(plot.getWorld()));
        if(plot.isRentable())
            new PlotExpireEvent(plot).callEvent();
    }

    public Plot getPlot(String regionID) {
        return this.plots.get(regionID);
    }

    public void savePlotGroup(PlotGroup group) {
        addon.getPlotGroupConfig()
             .set(String.valueOf(group.getGroupID()) + ".id", group.getGroupID())
             .set(String.valueOf(group.getGroupID()) + ".limit", Integer.valueOf(group.getPurchaseLimit()))
             .set(String.valueOf(group.getGroupID()) + ".rentDays", Integer.valueOf(group.getRentDays()))
             .set(String.valueOf(group.getGroupID()) + ".autoreset", Boolean.valueOf(group.isAutoReset()))
             .save();
    }

    public void deletePlotGroup(PlotGroup group) {
        this.groups.remove(group.getGroupID(), group);
        addon.getPlotGroupConfig()
             .set(group.getGroupID(), null)
             .save();
        Set<Plot> toDelete = (Set<Plot>)this.plots.values().stream().filter(plot -> (plot.getPlotGroup() == group)).collect(Collectors.toSet());
        toDelete.iterator().forEachRemaining(plot -> deletePlot(plot));
    }

    public PlotGroup getPlotGroup(String groupID) {
        return this.groups.get(groupID);
    }

    public PlotPlayer getPlayerData(UUID uuid) {
        return this.playerdata.computeIfAbsent(uuid, k -> new PlotPlayer(uuid));
    }

    public boolean isPlotRegion(String regionID) {
        return plots.containsKey(regionID);
    }

    public boolean canPurchasePlot(Player player, Plot plot) {
        if (plot.getOwner() != null)
            return false;
        if (!player.hasPermission(plot.getPlotGroup().getPermission()))
            return false;
        PlotPlayer pp = getPlayerData(player.getUniqueId());
        if (pp.getPlotAmount(plot.getPlotGroup()) >= plot.getPlotGroup().getPurchaseLimit())
            return false;
        return true;
    }

    public void expireCheck() {
        for (Plot plot : this.plots.values()) {
            if (!plot.isRentable())
                continue;
            if (plot.isExpired()) {
                new PlotExpireEvent(plot).callEvent();
                try {
                    resetPlot(plot);
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.addon.getPlugin().getLogger().info("Grundstück " + plot.getRegionID() + " ist abgelaufen und wurde zurückgesetzt.");
            }
        }
    }

    public boolean isValidPlotGroup(String group) {
        return this.groups.containsKey(group);
    }

    public Collection<Plot> getPlotMap() {
        return this.plots.values();
    }

    public HashMap<String, PlotGroup> getGroups() {
        return groups;
    }
}
