package de.pauerbanane.core.addons.vote.votechest.gui.admin;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.vote.votechest.VoteChest;
import de.pauerbanane.core.addons.vote.votechest.VoteChestContent;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.addons.vote.votechest.gui.VoteChestGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AdminContentSelectionGUI implements InventoryProvider {

    private VoteKey voteKey;

    private VoteChestManager manager;

    private VoteChest voteChest;

    public AdminContentSelectionGUI(VoteChestManager manager, VoteChest voteChest, VoteKey voteKey) {
        this.voteKey = voteKey;
        this.voteChest = voteChest;
        this.manager = manager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

        contents.set(1, 2, ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name(voteKey.getChestContent(VoteChestContent.Type.COMMON).getDisplayName()).build(), c -> {
            SmartInventory.builder().provider(new AdminContentEditGUI(manager, voteChest, voteKey, voteKey.getChestContent(VoteChestContent.Type.COMMON))).size(5).title(voteKey.getDisplayName() + "§7: " + voteKey.getChestContent(VoteChestContent.Type.COMMON).getDisplayName()).build().open(player);
        }));
        contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.GOLD_INGOT).name(voteKey.getChestContent(VoteChestContent.Type.RARE).getDisplayName()).build(), c -> {
            SmartInventory.builder().provider(new AdminContentEditGUI(manager, voteChest, voteKey, voteKey.getChestContent(VoteChestContent.Type.RARE))).size(5).title(voteKey.getDisplayName() + "§7: " + voteKey.getChestContent(VoteChestContent.Type.RARE).getDisplayName()).build().open(player);
        }));
        contents.set(1, 6, ClickableItem.of(new ItemBuilder(Material.NETHERITE_INGOT).name(voteKey.getChestContent(VoteChestContent.Type.EPIC).getDisplayName()).build(), c -> {
            SmartInventory.builder().provider(new AdminContentEditGUI(manager, voteChest, voteKey, voteKey.getChestContent(VoteChestContent.Type.EPIC))).size(5).title(voteKey.getDisplayName() + "§7: " + voteKey.getChestContent(VoteChestContent.Type.EPIC).getDisplayName()).build().open(player);
        }));

        contents.set(2,4, ClickableItem.of(new ItemBuilder(Material.EMERALD).name("§aHauptmenü").build(), c -> {
            SmartInventory.builder().provider(new VoteChestGUI(manager, voteChest)).size(3).title("§e§lVote-Chest").build().open(player);
        }));
    }

}
