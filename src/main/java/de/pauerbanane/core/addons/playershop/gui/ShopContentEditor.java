package de.pauerbanane.core.addons.playershop.gui;

import de.pauerbanane.api.chatinput.ChatInput;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.playershop.Shop;
import de.pauerbanane.core.addons.playershop.ShopContent;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopContentEditor implements InventoryProvider {

    private final Shop shop;

    private final ShopContent content;

    public ShopContentEditor(Shop shop, ShopContent content) {
        this.shop = shop;
        this.content = content;
    }

    public void init(Player player, InventoryContents contents) {
        contents.set(4, ClickableItem.empty(this.content.getItem().clone()));
        contents.set(SlotPos.of(1, 0), ClickableItem.of((new ItemBuilder(Material.GOLD_INGOT)).name("§2Verkaufspreis setzen").build(), click -> {
            new ChatInput(player, "Gib einen Betrag ein:", t -> {
                if(UtilMath.isDouble(t) && Double.parseDouble(t) > 0) {
                    double price = Double.parseDouble(t);
                    this.content.setSellPrice(price);
                } else
                    player.sendActionBar(F.error("Shop", "Ungültige Eingabe."));
            });
        }));
        String sellEnabled = "§4Deaktiviert";
        if (this.content.isSellEnabled())
            sellEnabled = "§2Aktiviert";
        contents.set(SlotPos.of(2, 0), ClickableItem.of((new ItemBuilder(this.content.isSellEnabled() ? Material.GREEN_DYE : Material.GRAY_DYE)).name("§6Verkauf: " + sellEnabled).build(), click -> {
            this.content.setSellEnabled(!this.content.isSellEnabled());
            reOpen(player, contents);
        }));
        contents.set(SlotPos.of(1, 1), ClickableItem.of((new ItemBuilder(Material.GOLD_INGOT)).name("§2Ankaufspreis setzen").build(), click -> {
            new ChatInput(player, "Gib einen Betrag ein:", t -> {
                if(UtilMath.isDouble(t) && Double.parseDouble(t) > 0) {
                    double price = Double.parseDouble(t);
                    this.content.setPurchasePrice(price);
                } else
                    player.sendActionBar(F.error("Shop", "Ungültige Eingabe."));
            });
        }));
        String enabled = "§4Deaktiviert";
        if (this.content.isPurchaseEnabled())
            enabled = "§2Aktiviert";
        contents.set(SlotPos.of(2, 1), ClickableItem.of((new ItemBuilder(this.content.isPurchaseEnabled() ? Material.GREEN_DYE : Material.GRAY_DYE)).name("§6Ankauf: " + enabled).build(), click -> {
            this.content.setPurchaseEnabled(!this.content.isPurchaseEnabled());
            reOpen(player, contents);
        }));
        contents.set(SlotPos.of(1, 4), ClickableItem.of((new ItemBuilder(Material.CHEST)).name("§6Lagerbestand").build(), click -> {
            this.content.openStockInventory(player, null);
        }));
        contents.set(SlotPos.of(2, 8), ClickableItem.of((new ItemBuilder(Material.TNT)).name("§4Slot löschen").build(), click -> {
            if (this.content.getStock() > 0) {
                player.sendMessage("Bitte leere zuerst den Lagerbestand");
                return;
            }
            this.shop.deleteContent(this.content);
            player.closeInventory();
        }));
    }
}
