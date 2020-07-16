package de.pauerbanane.core.addons.carriages.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.*;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.carriages.CarriageLine;
import de.pauerbanane.core.addons.carriages.countdowns.CarriageTeleportCountdown;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class CarriageLineGUI implements InventoryProvider {

    public static HashMap<UUID, CarriageTeleportCountdown> runningCountdowns = Maps.newHashMap();

    private CarriageLine carriageLine;

    public CarriageLineGUI(CarriageLine carriageLine) {
        this.carriageLine = carriageLine;
    }

    @Override
    public void init(Player player, InventoryContents content) {
        Pagination pagination = content.pagination();
        ArrayList<ClickableItem> items = Lists.newArrayList();
        content.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));
        content.fillRow(2, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        carriageLine.getCarriages().forEach(carriage -> {
            items.add(ClickableItem.of(carriage.getItem(), click -> {
                runningCountdowns.put(player.getUniqueId(), new CarriageTeleportCountdown(player, carriage));
                player.closeInventory();
            }));
        });

        ClickableItem[] c = new ClickableItem[items.size()];
        c = items.<ClickableItem>toArray(c);
        pagination.setItems(c);
        pagination.setItemsPerPage(9);


        if (items.size() > 0 && !pagination.isLast())
            content.set(4, 7, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite vor").build(), e -> {
                content.inventory().open(player, pagination.next().getPage());
            }));
        if (!pagination.isFirst())
            content.set(4, 1, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite zurück").build(), e -> {
                content.inventory().open(player, pagination.previous().getPage());
            }));


        SlotIterator slotIterator = content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0));
        slotIterator = slotIterator.allowOverride(false);
        pagination.addToIterator(slotIterator);
    }

    private void teleport(Player player, Location location, String name) {
        UtilPlayer.playSound(player, Sound.ENTITY_HORSE_ANGRY);

        int amount = 0;
        int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {
            UtilPlayer.playSound(player, Sound.ENTITY_HORSE_GALLOP);
        },0,10);
    }

}
