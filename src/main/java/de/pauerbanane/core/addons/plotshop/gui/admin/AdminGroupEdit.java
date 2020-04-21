package de.pauerbanane.core.addons.plotshop.gui.admin;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AdminGroupEdit implements InventoryProvider {

    private final PlotShop addon;

    private final PlotGroup group;

    public AdminGroupEdit(PlotShop addon, PlotGroup group) {
        this.addon = addon;
        this.group = group;
    }

    public void init(Player player, InventoryContents contents) {
        if (this.group == null) {
            player.sendMessage("Invalid Plotgroup");
            player.closeInventory();
            return;
        }
        contents.add(ClickableItem.of((new ItemBuilder(Material.STRUCTURE_VOID))
                .name("§7Limit pro Spieler: §a" + this.group.getPurchaseLimit())
                .lore("§7Die maximale Anzahl an Regionen von diesem Typ die ein Spieler")
                .lore("§7kaufen oder mieten darf.")
                .build(), e -> {
            if (e.isLeftClick()) {
                this.group.setPurchaseLimit(this.group.getPurchaseLimit() + 1);
                this.addon.getManager().savePlotGroup(this.group);
                contents.inventory().open(player);
            } else {
                this.group.setPurchaseLimit((this.group.getPurchaseLimit() == 0) ? 0 : (this.group.getPurchaseLimit() - 1));
                this.addon.getManager().savePlotGroup(this.group);
                contents.inventory().open(player);
            }
        }));
        contents.add(ClickableItem.of((new ItemBuilder(Material.CLOCK))
                .name("§7Mietbar in Tagen: §a" + ((this.group.getRentDays() == 0) ? "Permanent" : Integer.valueOf(this.group.getRentDays()).toString()))
                .lore("§7Zeit in Tagen um diese Region zu mieten.")
                .lore("§7Nach Ablauf der Zeit wird die Region zurückgesetzt falls §6Autoreset")
                .lore("§7aktiviert wurde.")
                .build(), e -> {
            if (e.isLeftClick()) {
                this.group.setRentDays(this.group.getRentDays() + 1);
                this.addon.getManager().savePlotGroup(this.group);
                contents.inventory().open(player);
            } else {
                this.group.setRentDays((this.group.getRentDays() == 0) ? 0 : (this.group.getRentDays() - 1));
                this.addon.getManager().savePlotGroup(this.group);
                contents.inventory().open(player);
            }
        }));
        String isAutoReset = "Ja";
        if (!this.group.isAutoReset())
            isAutoReset = "Nein";
        contents.add(ClickableItem.of((new ItemBuilder(Material.WOODEN_AXE))
                .name("§7Wird via Schematic zurückgesetzt: §a" + isAutoReset)
                .lore("§7Wenn die Region gelwird, wird automatisch eine")
                .lore("§7Schematic geladen von dem Zeitpunkt als die Region")
                .lore("§7erstellt wurde.")
                .build(), e -> {
            this.group.setAutoReset(!this.group.isAutoReset());
            this.addon.getManager().savePlotGroup(this.group);
            contents.inventory().open(player);
        }));
        contents.set(SlotPos.of(1, 8), ClickableItem.of((new ItemBuilder(Material.TNT)).name("§cGruppe löschen.").build(), e -> {
            if (e.isRightClick()) {
                this.addon.getManager().deletePlotGroup(this.group);
                SmartInventory.builder().provider(new AdminGroupList(this.addon)).title("Verfügbare Gruppen").size(3, 9).build().open(player);
            } else {
                player.sendMessage("Nutze Rechtsklick um die Gruppe zu löschen.");
            }
        }));
    }

    public void update(Player player, InventoryContents contents) {}
}