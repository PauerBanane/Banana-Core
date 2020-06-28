package de.pauerbanane.core.addons.resourcepack;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;

import java.io.File;

public class Resourcepack extends Addon {

    private ResourcepackManager manager;

    @Override
    public void onEnable() {
        this.manager = new ResourcepackManager(this);

        registerListener(new ResourcepackListener(manager));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }


    public FileLoader getResourcepackConfig() {
        return new FileLoader(plugin.getDataFolder(), "Resourcepack/config.yml");
    }

    public String getResourcepackHttpServerAddress() {
        FileLoader config = getResourcepackConfig();

        if(!config.isSet("IP")) {
            config.set("IP", "mc.knickscraft.de");
            config.save();
        }

        return config.getString("IP");
    }

    public int getPort() {
        FileLoader config = getResourcepackConfig();

        if(!config.isSet("Port")) {
            config.set("Port", 9554);
            config.save();
        }

        return config.getInt("Port");
    }
}
