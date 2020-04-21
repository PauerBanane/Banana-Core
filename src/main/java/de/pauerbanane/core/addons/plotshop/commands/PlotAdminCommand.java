package de.pauerbanane.core.addons.plotshop.commands;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import de.pauerbanane.core.addons.plotshop.gui.admin.AdminGroupList;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandAlias("plot")
@CommandPermission("command.plot.admin")
public class PlotAdminCommand extends BaseCommand {

    private PlotShop addon;

  public PlotAdminCommand(PlotShop addon) {
      this.addon = addon;
  }

  @Subcommand("editor")
  public void plotEditor(Player sender) {
      SmartInventory.builder().provider((InventoryProvider)new AdminGroupList(this.addon)).title("Verfügbare Gruppen").size(3, 9).build().open(sender);
  }

  @Subcommand("admin create")
  @CommandCompletion("@region @nothing @plotgroup @nothing")
  public void create(Player sender, String regionID, double price, PlotGroup plotGroup) {
      if(!WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sender.getWorld())).hasRegion(regionID)) {
          sender.sendMessage(F.error("Portal", "Es existiert keine Region mit dieser ID."));
          return;
      }
      if(addon.getManager().getPlot(regionID) != null) {
          sender.sendMessage(F.error("Plot", "Diese Region steht bereits zum Verkauf!"));
          return;
      }

      ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sender.getWorld())).getRegion(regionID);
      Plot plot = new Plot(regionID, region, sender.getWorld(), plotGroup);
      plot.setPrice(price);
      try {
          addon.getManager().registerPlot(plot);
      } catch (IOException e) {
          e.printStackTrace();
      }

      sender.sendMessage(F.main("Plot", "Die Region §e" + regionID + " §7steht nun zum Verkauf bereit."));
  }

  @Subcommand("admin remove")
  @CommandCompletion("@plot")
  public void delete(Player sender, Plot plot) {
      addon.getManager().deletePlot(plot);
      sender.sendMessage(F.main("Plot", "Du hast das Grundstück §e" + plot.getRegionID() + " §7gelöscht."));
  }

  @Subcommand("admin reset")
  @CommandCompletion("@plot")
  public void reset(Player sender, Plot plot) {
      try {
          addon.getManager().resetPlot(plot);
      } catch (MaxChangedBlocksException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
      }
      sender.sendMessage(F.main("Plot", "Du hast das Grundstück zurückgesetzt."));
  }

}
