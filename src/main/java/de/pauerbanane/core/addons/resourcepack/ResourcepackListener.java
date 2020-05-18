package de.pauerbanane.core.addons.resourcepack;

import com.google.common.collect.Sets;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.util.HashSet;
import java.util.UUID;

public class ResourcepackListener implements Listener {

    public ResourcepackListener(final ResourcepackManager manager) {
        this.manager = manager;
        attempts = Sets.newHashSet();
    }

    private final ResourcepackManager manager;
    private final HashSet<UUID> attempts;

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(BananaCore.getInstance(), () -> {
            sendResourcepack(player);
        }, 20L);
    }

    @EventHandler
    public void resourceStatusEvent(final PlayerResourcePackStatusEvent event) {

        final Player player = event.getPlayer();
        final UUID id = player.getUniqueId();
        final PlayerResourcePackStatusEvent.Status status = event.getStatus();
        if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            return;
        } else if (status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            if (attempts.contains(id)) {
                attempts.remove(id);
                player.kickPlayer("Bitte akzeptiere das Resourcepack\n -> Mehrspieler - KnicksCraft - Bearbeiten - Server-Ressourcenpakete: 'Abfrage'");
                return;
            } else {
                attempts.add(id);
                Bukkit.getScheduler().scheduleSyncDelayedTask(BananaCore.getInstance(), () -> {
                    sendResourcepack(player);
                }, 60L);
            }

        } else if (status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            player.kickPlayer("§fBitte akzeptiere das Resourcepack:\n§eMehrspieler §7- §eKnicksCraft §7- §eBearbeiten §7- §eServer-Ressourcenpakete: §7'§2Abfrage§7'\n§aVielen Dank! §e:)");
        }
    }

    private void sendResourcepack(final Player player) {
        player.setResourcePack(manager.getDownloadURL("knickscraft.zip"), manager.getResourceHash());
    }
}
