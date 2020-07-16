package de.pauerbanane.core.addons.plotshop.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import de.pauerbanane.core.addons.plotshop.gui.PlotManageGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("plot")
@CommandPermission("command.plot.user")
public class PlotUserCommand extends BaseCommand {

    private PlotShop addon;

    private WorldGuardPlatform wgPlatform;


    public PlotUserCommand(PlotShop addon) {
        this.addon = addon;
        this.wgPlatform = WorldGuard.getInstance().getPlatform();
    }

    @Default
    public void plotBase(Player sender) {
        RegionManager manager = this.wgPlatform.getRegionContainer().get(BukkitAdapter.adapt(sender.getWorld()));
        ApplicableRegionSet regions = manager.getApplicableRegions(BukkitAdapter.asBlockVector(sender.getLocation()));
        Optional<ProtectedRegion> region = regions.getRegions().stream().filter(rg -> rg.getOwners().contains(sender.getUniqueId()))
                .filter(rg -> this.addon.getManager().isPlotRegion(rg.getId())).findFirst();
        if (!region.isPresent()) {
            sender.sendMessage(F.error("Plots", "Du befindest dich nicht auf deinem Grundstück."));
            return;
        }
        Plot plot = this.addon.getManager().getPlot(((ProtectedRegion)region.get()).getId());
        SmartInventory.builder().provider((InventoryProvider)new PlotManageGUI(this.addon, plot)).title("Dein Grundstück").size(3).build().open(sender);
    }

    @Subcommand("list")
    public void list(Player sender) {
        sender.sendMessage(F.main("Plot", "Deine Grundstücke:"));
        addon.getManager().getPlayerData(sender.getUniqueId()).getPlots().forEach(plot -> sender.sendMessage(plot.getRegionID()));
    }

    @Subcommand("kaufen")
    public void purchase(Player sender) {
        if(!addon.cacheContains(sender)) {
            sender.sendMessage(F.error("Plot", "Du musst auf einem Grundstück stehen."));
            return;
        }
        Plot plot = addon.getTempPurchaseCache().get(sender.getUniqueId());
        if(plot == null) {
            sender.sendMessage(F.error("Plot", "Diese Region steht nicht zum Verkauf."));
            return;
        }
        if(!addon.getManager().canPurchasePlot(sender, plot)) {
            sender.sendMessage(F.error("Plot", "Du kannst dieses Grundstück nicht kaufen."));
            return;
        }
        if(BananaCore.getEconomy().withdrawPlayer(sender, plot.getPrice()).transactionSuccess()) {
            addon.getManager().purchasePlot(sender, plot);
            sender.sendMessage(F.main("Plot", "Du hast das Grundstück gekauft."));
            return;
        }
        sender.sendMessage(F.error("Plot", "Du hast nicht genügend Geld um das Grundstück zu kaufen."));
    }

    /*@Subcommand("verlängern")
    public void reRent(Player sender) {
        if(!addon.getReRentCache().containsKey(sender.getUniqueId())) {
            if (!addon.getTempReRentCache().containsKey(sender.getUniqueId())) {
                sender.sendMessage(F.error("Plot", "Du musst auf deinem Grundstück stehen."));
                return;
            }
            Plot plot = addon.getTempReRentCache().get(sender.getUniqueId());
            if (!plot.getOwner().equals(sender.getUniqueId())) {
                sender.sendMessage(F.error("Plot", "Du kannst kein fremdes Grundstück verlängern."));
                return;
            }
            if (!plot.isRentable()) {
                sender.sendMessage(F.error("Plot", "Du hast das Plot bereits gekauft."));
                return;
            }

            sender.sendMessage(F.main("Plot", "Du kannst dein Grundstück ab §ediesem Zeitpunkt"));
            sender.sendMessage(F.main("Plot", "um weitere §e" + plot.getPlotGroup().getRentDays() + " Tage §7verlängern."));
            sender.sendMessage(F.main("Plot", "Gebe dazu erneut §2/plot verlängern §7ein."));
            sender.sendMessage(F.main("Plot", "Kosten: §e" + plot.getPrice() + " Taler"));

            addon.getReRentCache().put(sender.getUniqueId(), plot);
        } else {
            Plot plot = addon.getReRentCache().get(sender.getUniqueId());
            if(BananaCore.getEconomy().withdrawPlayer(sender, plot.getPrice()).transactionSuccess()) {
                addon.getManager().reRentPlot(sender, plot);
            } else
                sender.sendMessage(F.error("Plot", "Du hast nicht genügend Geld, um dein Grundstück zu verlängern."));
        }
    }*/

    @Subcommand("info")
    @CommandCompletion("@plot")
    public void info(Player sender, Plot plot) {

        sender.sendMessage("§7§m===============§6 Grundstück §7§m===============");
        sender.sendMessage("§7Gruppe: §e" + plot.getPlotGroup().getGroupID());
        sender.sendMessage("§7Region: §e" + plot.getRegionID());
        sender.sendMessage("§7Preis: §e" + plot.getPrice());
        sender.sendMessage("§7Besitzer: §e" + ((plot.getOwner() == null) ? "Keiner" : Bukkit.getOfflinePlayer(plot.getOwner()).getName()));
        sender.sendMessage("§7Gekauft: §e" + plot.getPurchaseDateFormatted());
        sender.sendMessage("§7Bis: §e" + plot.getExpireDateFormatted());
    }

}
