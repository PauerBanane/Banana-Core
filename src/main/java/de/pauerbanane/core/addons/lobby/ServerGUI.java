package de.pauerbanane.core.addons.lobby;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerGUI implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents content) {
        content.fillRow(0, ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).build()));
        content.set(SlotPos.of(0,2), ClickableItem.of(new ItemBuilder(Material.DIAMOND).name("§6Survival").build(), e -> {
            sendToServer("survival", player);
        }));

        content.set(SlotPos.of(0, 4), ClickableItem.of(new ItemBuilder(Material.GRASS_BLOCK).name("§6SkyBlock").lore("§c Nicht verfügbar").build(), e -> {
            player.sendMessage(F.error("SkyBlock", "Dieser Server ist nicht online!"));
        }));

        content.set(SlotPos.of(0, 6), ClickableItem.of(new ItemBuilder(Material.QUARTZ_PILLAR).name("§6Kreativ").lore("§c Nicht verfügbar").build(), e -> {
            player.sendMessage(F.error("Kreativ", "Dieser Server ist nicht online!"));
        }));

    }

    private void sendToServer(final String server, final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(BananaCore.getInstance(), "BungeeCord", out.toByteArray());
    }

}