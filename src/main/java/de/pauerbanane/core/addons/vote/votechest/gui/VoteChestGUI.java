package de.pauerbanane.core.addons.vote.votechest.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.lobby.server.Server;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.VoteChest;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.addons.vote.votechest.gui.admin.AdminContentSelectionGUI;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VoteChestGUI implements InventoryProvider {

    private VoteChestManager manager;

    private VoteChest voteChest;

    public VoteChestGUI(VoteChestManager manager, VoteChest voteChest) {
        this.manager = manager;
        this.voteChest = voteChest;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        VoteData voteData = CorePlayer.get(player.getUniqueId()).getData(VoteData.class);

        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

        int i = 0;
        int pos = 2;
        for(VoteKey voteKey : manager.getVoteKeys().values()) {
            int row = 1 + ((int) i / 3);
            int obtainedKeys = voteData.getVoteKeys(voteKey);
            SlotPos slotPos = SlotPos.of(row, pos);

            contents.set(slotPos, ClickableItem.of(voteKey.getDescriptionItem(player), c -> {
                if (c.isLeftClick() && obtainedKeys > 0) {

                    voteData.removeVoteKey(voteKey, 1);
                    manager.startVoteChestEvent(player, voteChest, voteKey);
                    player.closeInventory();

                } else if (c.isRightClick() && player.hasPermission("command.votechest")) {

                    SmartInventory.builder().provider(new AdminContentSelectionGUI(manager, voteChest, voteKey)).title(voteKey.getDisplayName()).size(3).build().open(player);

                }
            }));

            switch (pos) {
                case 2:
                    pos = 4;
                    break;
                case 4:
                    pos = 6;
                    break;
                default:
                    pos = 2;
            }
            i++;
        }

    }

    @Override
    public void onClose(Player player, InventoryContents contents) {
        UtilPlayer.playSound(player, Sound.BLOCK_CHEST_CLOSE);
    }

}
