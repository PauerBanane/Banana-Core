package de.pauerbanane.core.data;

import com.google.common.collect.Maps;
import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.api.data.PlayerDataManager;
import de.pauerbanane.core.BananaCore;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class CorePlayer {

    private static HashMap<UUID, CorePlayer> players = Maps.newHashMap();

    private final UUID uuid;

    public UUID getUuid() {
        return this.uuid;
    }

    private static PlayerDataManager dataManager = PlayerDataManager.getInstance();

    public CorePlayer(UUID uuid) {
        players.put(uuid, this);
        this.uuid = uuid;
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            String prefix = PermissionManager.getPlayerPrefix(uuid);
            String suffix = PermissionManager.getPlayerSuffix(uuid);
            player.setDisplayName(prefix + ChatColor.GRAY + " " + ChatColor.WHITE + player.getName() + " " + suffix);
        }
    }

    public void reloadData() {
        dataManager.loadData(BananaCore.getInstance(), this.uuid);
    }

    public static CorePlayer get(UUID uuid) {
        if (!players.containsKey(uuid))
            return new CorePlayer(uuid);
        return players.get(uuid);
    }

    public void unload() {
        if (players.containsKey(this.uuid))
            players.remove(this.uuid);
    }

    public <T extends PlayerData> T getData(Class<? extends PlayerData> clazz) {
        return (T)dataManager.getPlayerData(this.uuid, BananaCore.getInstance(), clazz);
    }
}