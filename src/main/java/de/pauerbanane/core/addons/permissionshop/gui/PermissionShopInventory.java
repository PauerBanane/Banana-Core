package de.pauerbanane.core.addons.permissionshop.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.permissionshop.PermissionShopManager;
import de.pauerbanane.core.addons.permissionshop.ShopNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PermissionShopInventory implements InventoryProvider {

    private PermissionShopManager manager;

    private Economy eco;

    public PermissionShopInventory(PermissionShopManager manager) {
        this.manager = manager;
        this.eco = BananaCore.getEconomy();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        for(ShopNode shopNode : manager.getNodes()) {
            ItemStack icon = new ItemBuilder(shopNode.getMaterial()).setModelData(shopNode.getModelData()).lore(shopNode.getLore()).name(shopNode.getDisplayName()).build();
            icon.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            icon.addItemFlags(ItemFlag.HIDE_ENCHANTS);

            if(shopNode.hasBought(player)) {
                List<String> lore = icon.getLore();
                lore.add("");
                lore.add("§2Bereits gekauft");
                icon.setLore(lore);
                contents.add(ClickableItem.empty(icon));
            } else if(!shopNode.hasCondition(player)) {
                List<String> lore = icon.getLore();
                ShopNode condition = shopNode.getConditionNode();
                lore.add("§cDieses Paket benötigt zuvor das §6" + condition.getDisplayName() + " §cPaket.");
                icon.setLore(lore);
                contents.add(ClickableItem.empty(icon));
            } else if(!eco.has(player, shopNode.getPrice())) {
                List<String> lore = icon.getLore();
                lore.add("§cDu hast nicht genug Taler dabei");
                icon.setLore(lore);
                contents.add(ClickableItem.empty(icon));
            } else {
                contents.add(ClickableItem.of(icon, e -> {
                    SmartInventory.builder().title("Bestätigen").size(3, 9).provider(new ShopAcceptInventory(shopNode)).build().open(player);
                }));
            }
        }
    }
}
