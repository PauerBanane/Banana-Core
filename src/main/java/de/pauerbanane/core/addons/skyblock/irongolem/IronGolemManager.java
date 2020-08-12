package de.pauerbanane.core.addons.skyblock.irongolem;

import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.addons.skyblock.SkyblockAddon;
import de.pauerbanane.core.addons.skyblock.irongolem.commands.IrongolemCommand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class IronGolemManager implements Listener {

    private SkyblockAddon addon;

    private FileLoader config;

    private boolean abort = true;

    private int percentage = 20;

    public IronGolemManager(SkyblockAddon addon) {
        this.addon = addon;

        load();

        addon.registerCommand(new IrongolemCommand(this));
        addon.registerListener(this);
    }

    private void load() {
        this.config = addon.getConfig();
        if (!config.isSet("irongolemhandler")) {
            config.set("irongolemhandler.enabled", true);
            config.set("irongolemhandler.spawnchance", 20);
            config.save();
        }
        this.abort = config.getBoolean("irongolemhandler.enabled");
        this.percentage = config.getInt("irongolemhandler.spawnchance");
    }

    public void save() {
        config.set("irongolemhandler.enabled", abort);
        config.set("irongolemhandler.spawnchance", percentage);
        config.save();
    }

    @EventHandler
    public void handleIronGolemSpawn(CreatureSpawnEvent e) {
        if (!abort) return;
        if (e.getEntityType() != EntityType.IRON_GOLEM) return;
        System.out.println(e.getSpawnReason());
        if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.VILLAGE_DEFENSE) return;

        int random = UtilMath.getRandom(0,100);
        if (random >= percentage) {
            e.setCancelled(true);
            addon.getPlugin().getLogger().info("Aborted Irongolem Spawn");
        }
    }

    public void setAbort(boolean abort) {
        this.abort = abort;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public int getPercentage() {
        return percentage;
    }

    public boolean isAbort() {
        return abort;
    }
}
