package de.pauerbanane.core.addons.ranks.gui;

import com.google.common.collect.Lists;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilItem;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.ranks.Rank;
import de.pauerbanane.core.addons.ranks.RankManager;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class RankGUI implements InventoryProvider {

    private RankManager manager;

    public RankGUI(RankManager manager) {
        this.manager = manager;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ArrayList<ClickableItem> items = Lists.newArrayList();
        String rankName = PermissionManager.getApi().getGroupManager().getGroup(PermissionManager.getUser(player.getUniqueId()).getPrimaryGroup()).getDisplayName();

        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));

        contents.set(SlotPos.of(2, 4), ClickableItem.empty(new ItemBuilder(UtilItem.getPlayerHead(player.getUniqueId()))
                                                                                            .name("§7Dein Rang: §l" + rankName).build()));


        for (Rank r : manager.getRanks().values()) {
            boolean obtained = r.hasRank(player);
            boolean canObtain = r.hasAchievedConditions(player);
            if (obtained || !canObtain) {
                items.add(ClickableItem.empty(r.getDescriptionItem(player)));
            } else {
                items.add(ClickableItem.of(r.getDescriptionItem(player), c -> {
                    r.uprank(player);
                    player.closeInventory();
                    Bukkit.broadcastMessage(F.main("Rang", "Glückwunsch §e" + player.getName() + "§7! Du bist nun " + r.getGroup().getDisplayName() + "§7."));
                    player.sendMessage(F.main("Rang", "Die Änderungen werden nach einem Reconnect aktiviert."));
                    Bukkit.getOnlinePlayers().forEach(p -> UtilPlayer.playSound(p, Sound.ENTITY_PLAYER_LEVELUP));

                    PermissionManager.removeGroupNodes(player);
                    PermissionManager.addGroupNode(player, r.getGroup());
                }));
            }
        }


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
