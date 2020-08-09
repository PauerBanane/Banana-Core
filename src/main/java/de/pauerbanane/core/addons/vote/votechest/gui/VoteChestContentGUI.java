package de.pauerbanane.core.addons.vote.votechest.gui;

import com.google.common.collect.Lists;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotIterator;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.playershop.ShopContent;
import de.pauerbanane.core.addons.playershop.gui.ShopContentEditor;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Optional;

public class VoteChestContentGUI implements InventoryProvider {

    private VoteKey voteKey;

    private VoteKey.Rarity rarity;

    private int size = 27;

    private ArrayList<ItemStack> itemStacks;

    public VoteChestContentGUI(VoteKey voteKey, ArrayList<ItemStack> itemStacks, VoteKey.Rarity rarity) {
        this.voteKey = voteKey;
        this.itemStacks = itemStacks;
        this.rarity = rarity;
    }

    @Override
    public void init(Player player, InventoryContents content) {
        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));
        SlotIterator iterator = content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0));

        for (ItemStack itemStack : this.itemStacks) {
            iterator.next().set(ClickableItem.of(itemStack.clone(), click -> {
               click.getWhoClicked().setItemOnCursor(itemStack.clone());
               ItemStack toDelete = null;
               for (ItemStack item : itemStacks) {
                   if (item.equals(itemStack))
                       toDelete = item;
               }
               if(toDelete != null)
                   itemStacks.remove(toDelete);

               this.reOpen(player, content);
            }));
            if (iterator.ended())
                break;
        }

        for (int i = 0; i < size - this.itemStacks.size(); i++) {
            ItemStack icon = (new ItemBuilder(Material.BARRIER)).name("§8Unbelegter Slot").build();
            iterator.next().set(ClickableItem.of(icon, click -> {
                if (click.getCursor() == null || click.getCursor().getType() == Material.AIR)
                    return;
                ItemStack contentInput = click.getCursor().clone();
                itemStacks.add(contentInput);
                click.getView().setCursor(new ItemStack(Material.AIR));
                reOpen(player, content);
            }));
        }
    }

    @Override
    public void onClose(Player player, InventoryContents contents) {
        SlotIterator slotIterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1,0));
        ArrayList<ItemStack> items = Lists.newArrayList();

        for (int i = 0; i < 27; i++) {
            Optional<ClickableItem> optional = slotIterator.next().get();
            if(!optional.isPresent()) continue;
            ItemStack item = optional.get().getItem();
            if(item == null || item.getType() == Material.AIR || item.getType() == Material.BARRIER) continue;
            items.add(item.clone());
        }

        voteKey.setItems(items, rarity);
    }
}
