package de.pauerbanane.core.addons.plotshop.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                .lore("§7Klicke hier um die Mietdauer der Region")
                .lore("§7zu verlängern")
                .build();
        ItemStack deleteIcon = (new ItemBuilder(Material.TNT)).name("§4Grundstück aufgeben")
                .lore("§4Hier kannst du dein Grundstück aufgeben")
                .lore("§4Das Grundstück wird anschließend zurückgesetzt")
                .lore("§c§l<Coming soon>")
                .build();
        ItemStack sellIcon = (new ItemBuilder(Material.BOOKSHELF)).name("Grundstück verkaufen").lore("§c§l<Coming soon>").build();
        contents.set(0, ClickableItem.of(members, e -> SmartInventory.builder().provider(new PlotMemberGUI(this.plot)).title("Mitglieder").size(4).build().open(player)));
        contents.set(4, ClickableItem.empty(ploticon));
        if (this.plot.getPlotGroup().getRentDays() > 0)
            contents.set(5, ClickableItem.of(extendRent, e -> {
                if (BananaCore.getEconomy().withdrawPlayer((OfflinePlayer)player, this.plot.getPrice()).transactionSuccess()) {
                    this.plot.setExpireDate(this.plot.getExpireDate().plusDays(this.plot.getPlotGroup().getRentDays()));
                    this.addon.getManager().savePlot(this.plot);
                    player.sendMessage("Neues Ablaufdatum: " + this.plot.getExpireDateFormatted());
                    player.closeInventory();
                    return;
                }
                player.sendMessage("Du hast nicht genug Geld um die Mietdauer zu verlängern.");
            }));
        contents.set(8, ClickableItem.of(deleteIcon, e -> {

        }));
        contents.set(7, ClickableItem.of(sellIcon, e -> {

        }));
    }

    public void update(Player player, InventoryContents contents) {}
}