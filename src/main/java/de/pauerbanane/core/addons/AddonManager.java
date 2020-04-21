package de.pauerbanane.core.addons;

import com.google.common.collect.Lists;
import de.pauerbanane.core.BananaCore;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class AddonManager {

    private static AddonManager instance;

    private BananaCore plugin;

    private ArrayList<Addon> addons;

    private FileConfiguration config;

    public AddonManager(BananaCore plugin) {
        this.instance = this;
        this.plugin = plugin;
        this.addons = Lists.newArrayList();
        this.config = plugin.getConfig();
    }

    public void registerAddon(Addon addon, String name) {
        addon.name = name;

        if(!config.isSet("addons." + addon.name + ".Enabled")) {
            config.set("addons." + addon.name + ".Enabled", false);
            plugin.saveConfig();
        }

        addons.add(addon);

        if(!config.getBoolean("addons." + addon.name + ".Enabled")) {
            plugin.getLogger().info(addon.name + " wurde nicht aktiviert");
            return;
        }

        addon.enable();
        plugin.getLogger().info(addon.name + " wurde aktiviert");
    }

    public void enableAddon(Addon addon) {
        if(addon.enabled) return;

        config.set("addons." + addon.name + ".Enabled", true);
        plugin.saveConfig();

        addon.enable();
        plugin.getLogger().info(addon.name + " wurde aktiviert");
    }

    public void disableAddon(Addon addon) {
        if(!addon.enabled) return;

        config.set("addons." + addon.name + ".Enabled", false);
        plugin.saveConfig();

        addon.disable();
        plugin.getLogger().info(addon.name + " wurde deaktiviert");
    }

    public Addon getAddon(Class<? extends Addon> clazz) {
        for(Addon addon : addons)
            if(addon.getClass().getName().equals(clazz.getName()))
                return addon;

        return null;
    }

    public Addon getAddon(String name) {
        for(Addon addon : addons)
            if(addon.name.equalsIgnoreCase(name))
                return addon;

        return null;
    }

    public void shutdown() {
        addons.forEach(addon -> {
            if(addon.isEnabled())
                addon.disable();
        });
    }

    public ArrayList<Addon> getAddons() {
        return addons;
    }

    public static AddonManager getInstance() {
        return instance;
    }
}
