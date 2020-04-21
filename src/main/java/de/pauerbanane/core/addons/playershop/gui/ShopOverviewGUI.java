package de.pauerbanane.core.addons.playershop.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotIterator;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.playershop.Shop;
import de.pauerbanane.core.addons.playershop.ShopContent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class ShopOverviewGUI implements InventoryProvider {

    private final Shop shop;

    public ShopOverviewGUI(Shop shop) {
        this.shop = shop;
    }

    public void init(Player player, InventoryContents contents) {
        contents.set(0, ClickableItem.empty((new ItemBuilder(Material.COAL)).name(" ").setModelData(2000).build()));
        contents.set(8, ClickableItem.empty((new ItemBuilder(Material.COAL)).name(" ").setModelData(2001).build()));
        ItemBuilder builder = (new ItemBuilder(Material.GOLD_NUGGET)).name((this.shop.getShopName() == null) ? "Spielershop" : ChatColor.translateAlternateColorCodes('&', this.shop.getShopName()));
        builder.lore("§fBesitzer: §e" + this.shop.getLastKnownNickname()).lore("§fGuthaben: §e" + this.shop.getBalance());
        if (player.getUniqueId().equals(this.shop.getOwner()))
            builder.lore("").lore("§fKlicke hier um deinen Shop zu §averwalten§f.");
        contents.set(4, ClickableItem.of(builder.build(), click -> {
            if (player.getUniqueId().equals(this.shop.getOwner()))
                SmartInventory.builder().provider(new ShopOwnerGUI(this.shop)).title("Verwalte deinen Shop").size(6).build().open(player);
        }));
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(2, 0));
        for (Iterator<ShopContent> iterator1 = this.shop.getShopContent().iterator(); iterator1.hasNext(); ) {
            ShopContent val = iterator1.next();
            ItemStack icon = (new ItemBuilder(val.getItem().clone()))
                    .lore("")
                    .lore("§fVerfügbare Items: §e" + val.getStock())
                    .lore(val.getPriceLine())
                    .lore("")
                    .lore("§6>> §fLinksklick um 1 zu kaufen.")
                    .lore("§6>> §fShift-Linksklick um 64 zu kaufen.")
                    .lore("")
                    .lore("§6>> §fRechtsklickum 1 zu verkaufen.")
                    .lore("§6>> §fShift-Rechtsklickum 64 zu verkaufen.")
                    .build();
            iterator.next().set(ClickableItem.of(icon, click -> {
                if (click.getClick() == ClickType.SHIFT_LEFT || click.getClick() == ClickType.LEFT) {
                    int amount = (click.getClick() == ClickType.SHIFT_LEFT) ? 64 : 1;
                    if (val.purchase(player, amount)) {
                        player.sendMessage(F.main("Shop", "Du hast " + String.valueOf(amount) + "x " + val.getItem().getType().name() + " für " + String.valueOf(val.getSellPrice() * amount) + " §2gekauft."));
                        reOpen(player,contents);
                    }
                } else {
                    int amount = (click.getClick() == ClickType.SHIFT_RIGHT) ? 64 : 1;
                    if (val.sell(player, amount)) {
                        player.sendMessage(F.main("Shop", "Du hast " + String.valueOf(amount) + "x " + val.getItem().getType().name() + " für " + String.valueOf(val.getPurchasePrice() * amount) + " §2verkauft."));
                        reOpen(player, contents);
                    }
                }
            }));
            if (iterator.ended())
                break;
        }
    }
}
