package de.pauerbanane.core.addons.ranks.gui.admin;

import com.google.common.collect.Lists;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.ranks.Rank;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AdminRemoveRankConditionGUI implements InventoryProvider {

    private Rank rank;

    public AdminRemoveRankConditionGUI(Rank rank) {
        this.rank = rank;
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        Pagination pagination = contents.pagination();
        ArrayList<ClickableItem> items = Lists.newArrayList();
        contents.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build()));


        for (RankCondition condition : rank.getConditions()) {
            items.add(ClickableItem.of(new ItemBuilder(Material.OAK_SIGN).name("§7Voraussetzung:").lore(condition.requirementsAsLore(player)).lore("§4Rechtsklick zum Entfernen").build(), c -> {
                if (c.isRightClick()) {
                    player.closeInventory();
                    rank.removeCondition(condition);
                    reOpen(player, contents);
                }
            }));
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
