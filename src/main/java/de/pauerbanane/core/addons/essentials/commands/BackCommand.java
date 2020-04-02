package de.pauerbanane.core.addons.essentials.commands;

import com.google.common.collect.Maps;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.Addon;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import sun.jvm.hotspot.ui.ObjectHistogramPanel;

import java.util.HashMap;
import java.util.UUID;

@CommandAlias("back")
@CommandPermission("command.back")
public class BackCommand extends BaseCommand implements Listener {

    private HashMap<UUID, Location> backLocations;

    public BackCommand(Addon addon) {
        this.backLocations = Maps.newHashMap();
        addon.registerListener(this);
    }

    @Default
    public void onDefault(Player sender) {
        if(!backLocations.containsKey(sender.getUniqueId())) {
            sender.sendMessage(F.error("Back", "Es wurde in letzter Zeit kein Teleport von dir registriert."));
        } else
            sender.teleport(backLocations.get(sender.getUniqueId()));
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        backLocations.put(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player p = e.getEntity();

        backLocations.put(p.getUniqueId(), p.getLocation());
    }

    @EventHandler
    public void handleDisconnect(PlayerQuitEvent e) {
        if(backLocations.containsKey(e.getPlayer().getUniqueId()))
            backLocations.remove(e.getPlayer().getUniqueId());
    }

}