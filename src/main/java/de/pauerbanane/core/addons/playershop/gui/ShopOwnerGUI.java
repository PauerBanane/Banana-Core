package de.pauerbanane.core.addons.playershop.gui;

import de.pauerbanane.api.anvilgui.AnvilGUI;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotIterator;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.playershop.Shop;
import de.pauerbanane.core.addons.playershop.ShopContent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ShopOwnerGUI implements InventoryProvider {

    private final Shop shop;

    public ShopOwnerGUI(Shop shop) {
        this.shop = shop;
    }

    public void init(Player player, InventoryContents contents) {
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0));
        contents.set(2, ClickableItem.of((new ItemBuilder(Material.GOLD_INGOT))
                .name("§fShop Guthaben: §e" + this.shop.getBalance())
                .lore("")
                .lore("§aLinksklick §fum einen Betrag abzuheben.")
                .lore("§aRechtsklick §fum einen Betrag einzuzahlen.")
                .build(), click -> {
            Economy eco = BananaCore.getEconomy();
            if (click.getClick() == ClickType.LEFT) {
                new AnvilGUI.Builder()
                        .onComplete((p, t) -> {
                            if(UtilMath.isDouble(t) && Double.parseDouble(t) > 0) {
                                double withdraw = Double.parseDouble(t);
                                if(this.shop.getBalance() >= withdraw) {
                                    eco.depositPlayer(player, withdraw);
                                    this.shop.withdrawBalance(withdraw);
                                    return AnvilGUI.Response.close();
                                }
                                return AnvilGUI.Response.text("§cNicht genügend Geld");
                            }
                            return AnvilGUI.Response.text("§cUngültige Eingabe");
                        })
                        .title("§eGeld abheben")
                        .plugin(BananaCore.getInstance())
                        .open(player);
            } else if (click.getClick() == ClickType.RIGHT) {
                new AnvilGUI.Builder()
                        .onComplete((p, t) -> {
                            if(UtilMath.isDouble(t) && Double.parseDouble(t) > 0) {
                                double deposit = Double.parseDouble(t);
                                if(eco.withdrawPlayer(player, deposit).transactionSuccess()) {
                                    this.shop.depositBalance(deposit);
                                    return AnvilGUI.Response.close();
                                }
                                return AnvilGUI.Response.text("§cNicht genügend Geld");
                            }
                            return AnvilGUI.Response.text("§cUngültige Eingabe");
                        })
                        .title("§eGeld einzahlen")
                        .plugin(BananaCore.getInstance())
                        .open(player);
            }
        }));
        contents.set(4, ClickableItem.of((new ItemBuilder(Material.NAME_TAG))
                .name("§fAktueller Name: §e" + ChatColor.translateAlternateColorCodes('&', this.shop.getShopName()))
                .lore("")
                .lore("§fHier kannst du den §aNamen des")
                .lore("§aShops §fändern.")
                .build(), click -> {
            new AnvilGUI.Builder()
                    .onComplete((p, t) -> {
                        shop.setShopName(t);
                        return AnvilGUI.Response.close();
                    })
                    .title("§eShop-Name eingeben")
                    .text("")
                    .plugin(BananaCore.getInstance())
                    .open(player);
        }));
        contents.set(6, ClickableItem.of((new ItemBuilder(Material.VILLAGER_SPAWN_EGG))
                .name("§fAussehen: §e" +  this.shop.getProfession().getKey().getKey().toString())
                .lore("")
                .lore("§fHier kannst du das §aAussehen des")
                .lore("§aNPCs §fändern.")
                .build(), click -> SmartInventory.builder().provider(new ShopVillagerProfession(this.shop)).title("Aussehen ändern").size(2).build().open(player)));
        for (ShopContent content : this.shop.getShopContent()) {
            iterator.next().set(ClickableItem.of(content.getItem().clone(), click -> SmartInventory.builder().provider(new ShopContentEditor(this.shop, content)).title("Ware bearbeiten").size(4).build().open(player)));
            if (iterator.ended())
                break;
        }
        for (int i = 0; i < this.shop.getShopSlots() - this.shop.getShopContent().size(); i++) {
            ItemStack icon = (new ItemBuilder(Material.BARRIER)).name("§8Unbelegter Slot").build();
            iterator.next().set(ClickableItem.of(icon, click -> {
                if (click.getCursor() == null || click.getCursor().getType() == Material.AIR)
                    return;
                ItemStack contentInput = click.getCursor().clone();
                Optional<ShopContent> content = this.shop.addContent(contentInput);
                click.getView().setCursor(new ItemStack(Material.AIR));
                reOpen(player, contents);
            }));
        }
    }
}
