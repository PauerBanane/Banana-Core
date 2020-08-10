package de.pauerbanane.core.addons.vote.votechest.gui.admin;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotIterator;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.vote.votechest.VoteChest;
import de.pauerbanane.core.addons.vote.votechest.VoteChestContent;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.addons.vote.votechest.gui.VoteChestGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AdminContentEditGUI implements InventoryProvider {

    private VoteChestManager manager;

    private VoteChest voteChest;

    private VoteKey voteKey;

    private VoteChestContent chestContent;

    private int size = 27;

    public AdminContentEditGUI(VoteChestManager manager, VoteChest voteChest, VoteKey voteKey, VoteChestContent content) {
        this.manager = manager;
        this.voteChest = voteChest;
        this.voteKey = voteKey;
        this.chestContent = content;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

        SlotIterator iterator = contents.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0));

        for (ItemStack itemStack : chestContent.getContents()) {
            iterator.next().set(ClickableItem.of(itemStack.clone(), click -> {
                chestContent.removeContent(itemStack);

                this.reOpen(player, contents);
            }));
            if (iterator.ended())
                break;
        }

        for (int i = 0; i < size - this.chestContent.getContents().size(); i++) {
            ItemStack icon = (new ItemBuilder(Material.BARRIER)).name("§8Unbelegter Slot").build();
            iterator.next().set(ClickableItem.of(icon, click -> {
                if (click.getCursor() == null || click.getCursor().getType() == Material.AIR)
                    return;
                ItemStack contentInput = click.getCursor().clone();
                chestContent.addContent(contentInput);
                click.getView().setCursor(new ItemStack(Material.AIR));
                reOpen(player, contents);
            }));
        }

        contents.set(4, 4, ClickableItem.of(new ItemBuilder(Material.EMERALD).name("§aHauptmenü").build(), c -> {
            SmartInventory.builder().provider(new VoteChestGUI(manager, voteChest)).size(3).title("§e§lVote-Chest").build().open(player);
        }));
    }
}
