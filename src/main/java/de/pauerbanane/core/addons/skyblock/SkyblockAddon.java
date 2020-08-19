package de.pauerbanane.core.addons.skyblock;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.addons.skyblock.irongolem.IronGolemManager;
import de.pauerbanane.core.addons.skyblock.listener.SkyblockListener;

public class SkyblockAddon extends Addon {

    private FileLoader config;

    private IronGolemManager ironGolemManager;

    @Override
    public void onEnable() {
        this.config = new FileLoader(getAddonFolder(), "Skyblock.yml");
        this.ironGolemManager = new IronGolemManager(this);

        registerListener(new SkyblockListener(this));
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
