package de.pauerbanane.core.addons.vote.votechest.gui;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilItem;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.vote.votechest.VoteChest;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.addons.vote.data.VoteData;
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
    public void init(Player player, InventoryContents content) {
        VoteData voteData = CorePlayer.get(player.getUniqueId()).getData(VoteData.class);
        int oldKeys = voteData.getVoteKeys(VoteKey.Type.OLD_KEY),
            ancientKeys = voteData.getVoteKeys(VoteKey.Type.ANCIENT_KEY),
            epicKeys = voteData.getVoteKeys(VoteKey.Type.EPIC_KEY);

        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        content.set(SlotPos.of(2, 4), ClickableItem.empty(new ItemBuilder(UtilItem.getPlayerHead(player.getUniqueId())).name("§7Votes: §a§l" + voteData.getVotes()).build()));

        content.set(SlotPos.of(1, 2), ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name(VoteKey.getVoteKeyName(VoteKey.Type.OLD_KEY) + "§7: §r§a" + oldKeys).build(), click -> {
            if(voteChest.isOccupied()) {
                player.sendMessage(F.error("VoteChest", "Diese VoteChest wird gerade benutzt."));
                return;
            }
            if(oldKeys > 0) {
                manager.startVoteChestEvent(player, voteChest, VoteKey.Type.OLD_KEY);
                voteData.setVoteKeys(VoteKey.Type.OLD_KEY, oldKeys - 1);
                player.closeInventory();
            } else {
                player.sendMessage(F.error("VoteChest", "Dazu hast du nicht genug Schlüssel."));
            }
        }));
        content.set(SlotPos.of(1, 4), ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name(VoteKey.getVoteKeyName(VoteKey.Type.ANCIENT_KEY) + "§7: §r§a" + ancientKeys).build(), click -> {
            if(voteChest.isOccupied()) {
                player.sendMessage(F.error("VoteChest", "Diese VoteChest wird gerade benutzt."));
                return;
            }
            if(ancientKeys > 0) {
                manager.startVoteChestEvent(player, voteChest, VoteKey.Type.ANCIENT_KEY);
                voteData.setVoteKeys(VoteKey.Type.ANCIENT_KEY, ancientKeys - 1);
                player.closeInventory();
            } else {
                player.sendMessage(F.error("VoteChest", "Dazu hast du nicht genug Schlüssel."));
            }
        }));
        content.set(SlotPos.of(1, 6), ClickableItem.of(new ItemBuilder(Material.IRON_INGOT).name(VoteKey.getVoteKeyName(VoteKey.Type.EPIC_KEY) + "§7: §r§a" + epicKeys).build(), click -> {
            if(voteChest.isOccupied()) {
                player.sendMessage(F.error("VoteChest", "Diese VoteChest wird gerade benutzt."));
                return;
            }
            if(epicKeys > 0) {
                manager.startVoteChestEvent(player, voteChest, VoteKey.Type.EPIC_KEY);
                voteData.setVoteKeys(VoteKey.Type.EPIC_KEY, epicKeys - 1);
                player.closeInventory();
            } else {
                player.sendMessage(F.error("VoteChest", "Dazu hast du nicht genug Schlüssel."));
            }
        }));
    }

    @Override
    public void onClose(Player player, InventoryContents contents) {
        UtilPlayer.playSound(player, Sound.BLOCK_CHEST_CLOSE);
    }
}
