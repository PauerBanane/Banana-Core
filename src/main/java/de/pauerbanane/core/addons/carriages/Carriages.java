package de.pauerbanane.core.addons.carriages;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Carriages extends Addon {

    private CarriageManager manager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Carriage.class, "Carriage");
        ConfigurationSerialization.registerClass(CarriageLine.class, "CarriageLine");
        this.manager = new CarriageManager(this);

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }
}
