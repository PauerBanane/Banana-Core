package de.pauerbanane.core.addons.vote.votechest.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class VoteKeyContentSelectionGUI implements InventoryProvider {

    private VoteKey voteKey;

    public VoteKeyContentSelectionGUI(VoteKey voteKey) {
        this.voteKey = voteKey;
    }

    @Override
    public void init(Player player, InventoryContents content) {
        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        content.set(SlotPos.of(1, 2), ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name(VoteKey.getVoteKeyName(voteKey.getType()) + "§7: Standard Items").build(), click -> {
            SmartInventory.builder().provider(new VoteChestContentGUI(voteKey, voteKey.getCommonItems())).size(3).build().open(player);
        }));
        content.set(SlotPos.of(1, 4), ClickableItem.of(new ItemBuilder(Material.GOLD_INGOT).name(VoteKey.getVoteKeyName(voteKey.getType()) + "§7: §6Seltene §7Items").build(), click -> {
            SmartInventory.builder().provider(new VoteChestContentGUI(voteKey, voteKey.getRareItems())).size(3).build().open(player);
        }));
        content.set(SlotPos.of(1, 6), ClickableItem.of(new ItemBuilder(Material.NETHERITE_INGOT).name(VoteKey.getVoteKeyName(voteKey.getType()) + "§7: §5Epische §7Items").build(), click -> {
            SmartInventory.builder().provider(new VoteChestContentGUI(voteKey, voteKey.getVeryRareItems())).size(3).build().open(player);
        }));
    }
}
