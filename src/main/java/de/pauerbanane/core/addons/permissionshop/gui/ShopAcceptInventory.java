package de.pauerbanane.core.addons.permissionshop.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.permissionshop.ShopNode;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ShopAcceptInventory implements InventoryProvider {

    private ShopNode node;

    private Economy eco;

    public ShopAcceptInventory(ShopNode node) {
        this.node = node;
        this.eco = BananaCore.getEconomy();
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));
        contents.set(1, 3, ClickableItem.of(new ItemBuilder(Material.EMERALD_BLOCK).name("§2Kaufen").build(), e -> {
            if(!eco.has(player, node.getPrice())) return;
            eco.withdrawPlayer(player, node.getPrice());

            node.addPermissions(player);

            player.sendMessage(F.main("Rechte", "Du hast dir §6" + node.getDisplayName() + " §7gekauft."));
            player.closeInventory();
        }));

        contents.set(1, 5, ClickableItem.of(new ItemBuilder(Material.REDSTONE_BLOCK).name("§cAbbrechen").build(), e -> {
            player.closeInventory();
        }));
    }
}
