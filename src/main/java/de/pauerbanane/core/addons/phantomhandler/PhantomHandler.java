package de.pauerbanane.core.addons.phantomhandler;

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.settings.data.Settings;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class PhantomHandler extends Addon implements Listener {

    @Override
    public void onEnable() {
        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void handlePhantomSpawn(PhantomPreSpawnEvent e) {
        if(!(e.getReason() == CreatureSpawnEvent.SpawnReason.NATURAL)) return;
        if(!(e.getSpawningEntity() instanceof Player)) return;
        Player player = (Player) e.getSpawningEntity();
        CorePlayer cp = CorePlayer.get(player.getUniqueId());
        Settings settings = cp.getData(Settings.class);
        if(!settings.phantomSpawnEnabled()) {
            e.setCancelled(true);
            plugin.getLogger().info("Aborted phantom spawn from Player " + player.getName());
        }
    }

}
