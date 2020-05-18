package de.pauerbanane.core.addons.lobby.server;


import de.pauerbanane.api.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Server {

    private String displayName;

    private String targetServer;

    private Material icon;

    private List<String> lore;

    private String permission;

    private boolean showWithoutPermission;

    public Server(String displayName, String targetServer, Material icon, List<String> lore, String permission, boolean showWithoutPermission) {
        this.displayName = displayName;
        this.targetServer = targetServer;
        this.icon = icon;
        this.lore = lore;
        this.permission = permission;
        this.showWithoutPermission = showWithoutPermission;
    }

    public boolean hasPermission(Player player) {
        return player.hasPermission(permission);
    }

    public boolean isVisible(Player player) {
        return showWithoutPermission ? true : player.hasPermission(permission);
    }

    public ItemStack getItem() {
        return new ItemBuilder(icon).name(displayName).lore(lore).build();
    }

    public String getTargetServer() {
        return targetServer;
    }
}
