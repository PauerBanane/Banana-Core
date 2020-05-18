package de.pauerbanane.core.addons.plotshop;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.api.regionevents.RegionLeaveEvent;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.plotshop.commands.PlotAdminCommand;
import de.pauerbanane.core.addons.plotshop.commands.PlotUserCommand;
import de.pauerbanane.core.addons.plotshop.listener.ExpireListener;
import de.pauerbanane.core.addons.plotshop.scoreboard.PlotBoard;
import de.pauerbanane.core.addons.plotshop.scoreboard.PurchasedPlotBoard;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlotShop extends Addon implements Listener {

    private static PlotShop instance;

    private PlotManager manager;

    private FileLoader plotGroupConfig;

    private HashMap<UUID, Plot> tempPurchaseCache;

    private HashMap<UUID, Plot> tempReRentCache;

    private HashMap<UUID, Plot> reRentCache;

    @Override
    public void onEnable() {
        this.instance = this;
        this.tempPurchaseCache = Maps.newHashMap();
        this.tempReRentCache = Maps.newHashMap();
        this.reRentCache = Maps.newHashMap();
        ConfigurationSerialization.registerClass(Plot.class, "plot");
        this.plotGroupConfig = new FileLoader(getAddonFolder() + "PlotGroups.yml");
        this.manager = new PlotManager(this);

        manager.load();
        Bukkit.getScheduler().runTaskTimer(plugin, new ExpireListener(this), 100L, 600L);

        registerCommandContext();
        registerCommand(new PlotUserCommand(this));
        registerCommand(new PlotAdminCommand(this));

        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegionEnter(RegionEnterEvent e) {
        if(!manager.isPlotRegion(e.getRegion().getId())) return;

        Plot plot = manager.getPlot(e.getRegion().getId());
        Player player = e.getPlayer();
        if(plot.getOwner() == null) {
            tempPurchaseCache.put(player.getUniqueId(), plot);
            BananaCore.getScoreboardAPI().getBoardManager().setBoard(e.getPlayer(), new PlotBoard(plot));
        } else if(player.getUniqueId().equals(plot.getOwner()) && plot.isRentable() && plot.isAboutToExpire()) {
            player.sendMessage(F.main("Plot", "Dein Grundstück §e" + plot.getRegion().getId() + " §7wird in §e" + plot.getExpireHours() + " Stunden §7ablaufen."));
            player.sendMessage(F.main("Plot", "Gebe §2/plot verlängern §7ein, um die Mietdauer zu verlängern."));
        }

        if(plot.getOwner() != null && plot.getOwner().equals(player.getUniqueId()) && plot.isRentable())
            tempReRentCache.put(player.getUniqueId(), plot);

        if(plot.getOwner() != null)
            BananaCore.getScoreboardAPI().getBoardManager().setBoard(e.getPlayer(), new PurchasedPlotBoard(plot));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegionLeave(RegionLeaveEvent e) {
        if(!manager.isPlotRegion(e.getRegion().getId())) return;
        tempPurchaseCache.remove(e.getPlayer().getUniqueId());
        tempReRentCache.remove(e.getPlayer().getUniqueId());
        reRentCache.remove(e.getPlayer().getUniqueId());

        BananaCore.getScoreboardAPI().getBoardManager().resetBoard(e.getPlayer());
    }


    private void registerCommandContext() {
        commandManager.getCommandContexts().registerContext(PlotGroup.class, c -> {
            final String tag = c.popFirstArg();
            if(manager.getPlotGroup(tag) != null) {
                return manager.getPlotGroup(tag);
            } else
                throw new InvalidCommandArgument("Invalid PlotGroup specified.");
        });
        commandManager.getCommandCompletions().registerCompletion("plotgroup", c -> {
            return ImmutableList.copyOf(manager.getGroups().keySet());
        });

        commandManager.getCommandContexts().registerContext(Plot.class, c -> {
            String arg = c.popFirstArg();
            Plot plot = manager.getPlot(arg);
            if(plot == null) {
                throw new InvalidCommandArgument("There is no plot with the given id: " + arg);
            } else
                return plot;
        });
        commandManager.getCommandCompletions().registerCompletion("plot", c -> {
            return (Collection)manager.getPlotMap().stream().map(Plot::getRegionID).collect(Collectors.toSet());
        });
    }

    public boolean cacheContains(Player player) {
        return tempPurchaseCache.containsKey(player.getUniqueId());
    }

    public static PlotShop getInstance() {
        return instance;
    }

    public PlotManager getManager() {
        return manager;
    }

    public String getPlotFolder() {
        String path = plugin.getDataFolder().getPath() + File.separator + "plots";
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();

        return path;
    }

    public String getSchematicFolder() {
        String path = plugin.getDataFolder().getPath() + File.separator + "plots" + File.separator + "schematics";
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();

        return path;
    }

    public FileLoader getPlotGroupConfig() {
        return plotGroupConfig;
    }

    public HashMap<UUID, Plot> getTempPurchaseCache() {
        return tempPurchaseCache;
    }

    public HashMap<UUID, Plot> getReRentCache() {
        return reRentCache;
    }

    public HashMap<UUID, Plot> getTempReRentCache() {
        return tempReRentCache;
    }
}
