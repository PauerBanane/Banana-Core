package de.pauerbanane.core.addons.discovery.gui;

import com.google.common.collect.Lists;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.*;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilItem;
import de.pauerbanane.core.addons.discovery.Discovery;
import de.pauerbanane.core.addons.discovery.DiscoveryAddon;
import de.pauerbanane.core.addons.discovery.data.DiscoveryData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class DiscoveryListGUI implements InventoryProvider {

    private DiscoveryAddon addon;

    public DiscoveryListGUI(DiscoveryAddon addon) {
        this.addon = addon;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        DiscoveryData data = CorePlayer.get(player.getUniqueId()).getData(DiscoveryData.class);
        Pagination pagination = contents.pagination();
        ArrayList<ClickableItem> items = Lists.newArrayList();
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));


        for (Discovery discovery : addon.getDiscoveries().values()) {
            items.add(ClickableItem.empty(discovery.getDisplayItem(player)));
        }

        ItemStack stats = new ItemBuilder(UtilItem.getPlayerHead(player.getUniqueId()))
                                         .name("§eStatistik")
                                         .lore("§7Gefunden§8: §e" + data.getTotalUnlockedDiscoveries() + "§8/§a" + addon.getDiscoveries().values().size())
                                         .build();

        contents.set(SlotPos.of(2, 4), ClickableItem.empty(stats));


        ClickableItem[] c = new ClickableItem[items.size()];
        c = items.<ClickableItem>toArray(c);
        pagination.setItems(c);
        pagination.setItemsPerPage(7);
        if (items.size() > 0 && !pagination.isLast())
            contents.set(2, 7, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite vor").build(), e -> {
                contents.inventory().open(player, pagination.next().getPage());
            }));
        if (!pagination.isFirst())
            contents.set(2, 1, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite zurück").build(), e -> {
                contents.inventory().open(player, pagination.previous().getPage());
            }));

        SlotIterator slotIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1,1));
        slotIterator = slotIterator.allowOverride(true);
        pagination.addToIterator(slotIterator);
    }

}
