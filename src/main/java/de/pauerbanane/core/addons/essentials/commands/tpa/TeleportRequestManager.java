package de.pauerbanane.core.addons.essentials.commands.tpa;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.Addon;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class TeleportRequestManager {

    private final HashMap<UUID, Cache<UUID, TeleportRequestType>> requests;

    private final RemovalListener<UUID, TeleportRequestType> listener;

    public TeleportRequestManager(Addon addon) {
        addon.registerCommand(new TpaCommand(this));

        this.requests = Maps.newHashMap();
        this.listener = new RemovalListener<UUID, TeleportRequestType>() {
            public void onRemoval(RemovalNotification<UUID, TeleportRequestManager.TeleportRequestType> notification) {
                Player requester = Bukkit.getPlayer((UUID)notification.getKey());
                if (requester == null)
                    return;
                requester.sendMessage(F.main("Teleport", "Deine Teleportanfrage ist abgelaufen."));
            }
        };
    }

    public boolean addRequest(Player requester, Player target, TeleportRequestType type) {
        if (!this.requests.containsKey(target.getUniqueId())) {
            this.requests.put(target.getUniqueId(), CacheBuilder.newBuilder().maximumSize(50L).expireAfterWrite(40L, TimeUnit.SECONDS).build());
            ((Cache)this.requests.get(target.getUniqueId())).put(requester.getUniqueId(), type);
            return true;
        }
        ((Cache)this.requests.get(target.getUniqueId())).cleanUp();
        if (((Cache)this.requests.get(target.getUniqueId())).asMap().containsKey(requester.getUniqueId())) {
            requester.sendMessage(F.main("Teleport", "Du hast " + F.name(target.getDisplayName()) + " bereits eine Anfrage geschickt."));
            return false;
        }
        ((Cache)this.requests.get(target.getUniqueId())).put(requester.getUniqueId(), type);
        return true;
    }

    public boolean hasRequest(Player player) {
        if (!this.requests.containsKey(player.getUniqueId()))
            return false;
        ((Cache)this.requests.get(player.getUniqueId())).cleanUp();
        return (((Cache)this.requests.get(player.getUniqueId())).size() > 0L);
    }

    public Set<UUID> getActiveRequests(Player player) {
        if (!this.requests.containsKey(player.getUniqueId()))
            return Sets.newHashSet();
        ((Cache)this.requests.get(player.getUniqueId())).cleanUp();
        return ((Cache)this.requests.get(player.getUniqueId())).asMap().keySet();
    }

    public void denyRequest(Player player, UUID targetID) {
        if (this.requests.containsKey(player.getUniqueId()) && (
                (Cache)this.requests.get(player.getUniqueId())).asMap().containsKey(player.getUniqueId()))
            ((Cache)this.requests.get(player.getUniqueId())).invalidate(targetID);
    }

    public void acceptRequest(Player player, UUID targetID) {
        Player target = Bukkit.getPlayer(targetID);
        if (target == null) {
            player.sendMessage(F.main("Teleport", "Teleport abgebrochen, Spieler ist nicht mehr online."));
            return;
        }
        target.sendMessage(F.main("Teleport", String.valueOf(F.name(target.getDisplayName())) + " hat deine Teleportanfrage angenommen. Teleportiere..."));
        target.teleport((Entity)player);
        ((Cache)this.requests.get(player.getUniqueId())).invalidate(targetID);
        UtilPlayer.playSound(target, Sound.ENTITY_ENDERMAN_TELEPORT, 0.5F, 1.0F);
    }

    public enum TeleportRequestType {
        TELEPORT_TO, TELEPORT_HERE;
    }
}