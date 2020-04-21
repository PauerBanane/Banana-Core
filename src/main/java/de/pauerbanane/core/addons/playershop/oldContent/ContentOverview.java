package de.pauerbanane.core.addons.playershop.oldContent;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotIterator;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.playershop.PlayerShop;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class ContentOverview implements InventoryProvider {

    private ArrayList<ItemStack> items;

    public ContentOverview(ArrayList<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0));

        for(int i = 0; i < items.size(); i++) {
            ItemStack item = items.get(i);
            iterator.next().set(ClickableItem.of(new ItemBuilder(item.clone().asOne()).name("Ware zurücknehmen").build(), e -> {
                PlayerShop.getInstance().getStorageManager().registerStoragedEditor(player, item);
            }));
        }
    }

}
