package de.pauerbanane.core.addons.vote.votechest.gui;

import com.mysql.fabric.xmlrpc.base.Array;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class VoteChestContentGUI implements InventoryProvider {

    private VoteKey voteKey;

    private int size = 27;

    private ArrayList<ItemStack> itemStacks;

    public VoteChestContentGUI(VoteKey voteKey, ArrayList<ItemStack> itemStacks) {
        this.voteKey = voteKey;
        this.itemStacks = itemStacks;
    }

    @Override
    public void init(Player player, InventoryContents content) {
        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        //TODO Content von PlayerShop abgucken
    }
}
