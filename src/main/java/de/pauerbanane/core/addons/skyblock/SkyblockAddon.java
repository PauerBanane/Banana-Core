package de.pauerbanane.core.addons.skyblock;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.addons.skyblock.irongolem.IronGolemManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SkyblockAddon extends Addon {

    private FileLoader config;

    private IronGolemManager ironGolemManager;

    @Override
    public void onEnable() {
        this.config = new FileLoader(getAddonFolder(), "Skyblock.yml");
        this.ironGolemManager = new IronGolemManager(this);
    }

    @Override
    public void onDisable() {
        ironGolemManager.save();
    }

    @Override
    public void onReload() {

    }

    public FileLoader getConfig() {
        return config;
    }
}
