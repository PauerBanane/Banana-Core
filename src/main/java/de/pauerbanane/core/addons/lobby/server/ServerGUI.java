package de.pauerbanane.core.addons.lobby.server;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerGUI implements InventoryProvider {

    private ServerInterfaceManager manager;

    public ServerGUI(ServerInterfaceManager manager) {
        this.manager = manager;
    }

    @Override
    public void init(Player player, InventoryContents content) {
        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        int i = 0;
        int pos = 2;
        for(Server server : manager.getServers()) {
            int row = 1 + ((int) i / 3);
            if(server.hasPermission(player)) {
                content.set(SlotPos.of(row, pos), ClickableItem.of(server.getItem(), click -> {
                    sendToServer(server.getTargetServer(), player);
                }));
            } else if(server.isVisible(player))
                content.set(SlotPos.of(row, pos), ClickableItem.empty(new ItemBuilder(server.getItem()).clearLore().lore("§cDer Server ist nicht verfügbar").build()));

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

    private void sendToServer(final String server, final Player player) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(BananaCore.getInstance(), "BungeeCord", out.toByteArray());
    }

}