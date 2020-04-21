package de.pauerbanane.core.addons.permissionshop;

import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilFile;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.permissionshop.npc.PermissionShopAttachment;

import java.io.File;

public class PermissionShop extends Addon {

    private PermissionShopManager manager;

    private FileLoader config;

    @Override
    public void onEnable() {
        this.manager = new PermissionShopManager(this);
        UtilFile.copyResource(plugin.getResource("PermissionShop.yml"), new File(getAddonFolder(), "PermissionShop.yml"));
        this.config = new FileLoader(getAddonFolder() + "PermissionShop.yml");

        manager.load();

        registerNPCAttachment(new PermissionShopAttachment(this));
    }

    @Override
    public void onDisable() {

    }

    public FileLoader getConfig() {
        return config;
    }

    public PermissionShopManager getManager() {
        return manager;
    }
}
