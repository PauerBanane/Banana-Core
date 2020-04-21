package de.pauerbanane.core.addons.tab;

import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.data.PermissionManager;
import me.neznamy.tab.api.EnumProperty;
import me.neznamy.tab.api.TABAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Tab extends Addon implements Listener {

    private TABAPI api;

    @Override
    public void onEnable() {
        this.api = new TABAPI();
        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        String prefix = PermissionManager.getPlayerPrefix(e.getPlayer().getUniqueId());
        String suffix = PermissionManager.getPlayerSuffix(e.getPlayer().getUniqueId());
        api.setValueTemporarily(e.getPlayer().getUniqueId(), EnumProperty.TABPREFIX, suffix + " " + prefix + " | ");
    }

}
