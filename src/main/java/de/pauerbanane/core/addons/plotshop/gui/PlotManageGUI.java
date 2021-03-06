package de.pauerbanane.core.addons.plotshop.gui;

import com.sk89q.worldedit.MaxChangedBlocksException;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.smartInventory.inventories.ConfirmationGUI;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class PlotManageGUI implements InventoryProvider {

    private final Plot plot;

    private final PlotShop addon;

    private ItemStack border = (new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)).name(" ").build();

    public PlotManageGUI(PlotShop addon, Plot plot) {
        this.addon = addon;
        this.plot = plot;
    }

    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(this.border));
        ItemStack members = (new ItemBuilder(Material.PLAYER_HEAD)).name("§7Mitglieder")
                .lore("§7Klicke hier um die Mitglieder dieser Region zu verwalten.")
                .build();
        ItemStack ploticon = (new ItemBuilder(Material.BOOKSHELF)).name("§7Grundstück")
                .lore("§7Gruppe: §f" + this.plot.getPlotGroup().getGroupID())
                .lore("§7Region: §f" + this.plot.getRegionID())
                .lore("§7Gekauft am: §f" + this.plot.getPurchaseDateFormatted())
                .lore("§7Läuft ab in: §f" + this.plot.getExpireDateFormatted())
                .build();
        ItemStack extendRent = (new ItemBuilder(Material.GOLD_NUGGET)).name("§7Miete verlängern")
                .lore("§7Klicke hier um die §eMietdauer §7der Region")
                .lore("§7zu verlängern")
                .build();
        ItemStack deleteIcon = (new ItemBuilder(Material.TNT)).name("§4§lGrundstück aufgeben")
                .lore("§7Hier kannst du dein Grundstück §caufgeben")
                .lore("§7Das Grundstück wird anschließend §czurückgesetzt")
                .build();
        ItemStack sellIcon = (new ItemBuilder(Material.BOOKSHELF)).name("Grundstück verkaufen").lore("§c§l<Coming soon>").build();
        contents.set(SlotPos.of(1,0), ClickableItem.of(members, e -> SmartInventory.builder().provider(new PlotMemberGUI(this.plot)).title("Mitglieder").size(4).build().open(player)));
        contents.set(SlotPos.of(1,4), ClickableItem.empty(ploticon));
        if (this.plot.getPlotGroup().getRentDays() > 0)
            contents.set(SlotPos.of(1,5), ClickableItem.of(extendRent, e -> {
                if (BananaCore.getEconomy().withdrawPlayer((OfflinePlayer)player, this.plot.getPrice()).transactionSuccess()) {
                    this.plot.setExpireDate(this.plot.getExpireDate().plusDays(this.plot.getPlotGroup().getRentDays()));
                    this.addon.getManager().savePlot(this.plot);
                    player.sendMessage(F.main("Plots", "Neues Ablaufdatum: §a" + this.plot.getExpireDateFormatted()));
                    player.closeInventory();
                    addon.getManager().savePlot(plot);
                    return;
                }
                player.sendMessage(F.error("Plots", "Du hast nicht genug Geld um die Mietdauer zu verlängern."));
            }));
        contents.set(SlotPos.of(1,8), ClickableItem.of(deleteIcon, e -> {
            ConfirmationGUI.open(player, "§cGrundstück zurücksetzen", bool -> {
                if(bool) {
                    try {
                        addon.getManager().resetPlot(plot);
                        player.sendMessage(F.main("Plots", "Dein Grundstück wurde zurückgesetzt."));
                    } catch (MaxChangedBlocksException maxChangedBlocksException) {
                        maxChangedBlocksException.printStackTrace();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                        addon.getPlugin().getLogger().severe("Failed to reset Plot " + plot.getRegionID());
                    }
                }
                player.closeInventory();
            });
        }));
        contents.set(SlotPos.of(1,7), ClickableItem.of(sellIcon, e -> {

        }));
    }

    public void update(Player player, InventoryContents contents) {}
}