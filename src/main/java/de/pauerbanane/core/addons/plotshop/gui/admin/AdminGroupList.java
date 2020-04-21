package de.pauerbanane.core.addons.plotshop.gui.admin;


import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import de.pauerbanane.core.addons.plotshop.PlotManager;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class AdminGroupList implements InventoryProvider {

    private final PlotManager manager;

    private final PlotShop addon;

    public AdminGroupList(PlotShop addon) {
        this.addon = addon;
        this.manager = addon.getManager();
    }

    public void init(Player player, InventoryContents contents) {
        contents.set(SlotPos.of(2, 4), ClickableItem.of((new ItemBuilder(Material.EMERALD)).name("§2Neue Gruppe hinzufügen.").build(), e -> {
            player.closeInventory();
        }));
        for (PlotGroup group : this.manager.getPlotGroups())
            contents.add(ClickableItem.of((new ItemBuilder(Material.BOOK))
                    .name(group.getGroupID())
                    .lore("§7Anzahl pro Spieler: §e" + group.getPurchaseLimit())
                    .lore("§7Mietbar für: §e" + ((group.getRentDays() == 0) ? "permanent" : (String.valueOf(group.getRentDays()) + " Tage")))
                    .build(), e -> SmartInventory.builder().provider(new AdminGroupEdit(this.addon, group)).title("Gruppeneditor - " + group.getGroupID()).size(3, 9).build().open(player)));
    }

    public void update(Player player, InventoryContents contents) {}
}