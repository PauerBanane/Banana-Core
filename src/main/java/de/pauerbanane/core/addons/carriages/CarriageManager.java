package de.pauerbanane.core.addons.carriages;

import com.google.common.collect.Maps;
import de.pauerbanane.api.util.FileLoader;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CarriageManager {

    private Carriages addon;

    private FileLoader config;

    private HashMap<String, CarriageLine> carriageLines;

    public CarriageManager(Carriages addon) {
        this.addon = addon;
        this.carriageLines = Maps.newHashMap();
        this.config = new FileLoader(addon.getAddonFolder(), "Carriages.yml");

        load();
    }

    public boolean createNewLine(String name, World world) {
        if(getCarriageLine(name) != null) return false;
        CarriageLine carriageLine = new CarriageLine(name, world.getName());
        carriageLines.put(carriageLine.getName(), carriageLine);
        return true;
    }

    public boolean removeLine(CarriageLine line) {
        if(!carriageLines.values().contains(line)) return false;
        carriageLines.remove(line.getName());
        return true;
    }

    public boolean isCarriageRegion(String regionID, String worldName) {
        for(CarriageLine carriageLine : carriageLines.values()) {
            if (carriageLine.getWorld().equals(worldName)) {
                for (Carriage carriage : carriageLine.getCarriages()) {
                    if (carriage.getRegionID().equals(regionID))
                        return true;
                }
            }
        }
        return false;
    }

    public Carriage getCarriageByRegion(String regionID, String worldName) {
        for(CarriageLine carriageLine : carriageLines.values()) {
            if (carriageLine.getWorld().equals(worldName)) {
                for (Carriage carriage : carriageLine.getCarriages()) {
                    if (carriage.getRegionID().equals(regionID))
                        return carriage;
                }
            }
        }
        return null;
    }

    private void load() {
        if(!config.isSet("carriageLines")) return;

        ConfigurationSection section = config.getConfigurationSection("carriageLines");
        for (String line : section.getKeys(false)) {
            CarriageLine carriageLine = section.getSerializable(line, CarriageLine.class);
            carriageLines.put(carriageLine.getName(), carriageLine);

            addon.getPlugin().getLogger().info("Loaded Carriage-Line " + carriageLine.getName());
        }
        addon.getPlugin().getLogger().info("Loaded " + carriageLines.size() + " Carriage-Lines");

        if(!config.isSet("carriages")) return;

        section = config.getConfigurationSection("carriages");
        for (String carri : section.getKeys(false)) {
            Carriage carriage = section.getSerializable(carri, Carriage.class);
            if(getCarriageLine(carriage.getTempCarriageLineName()) != null) {
                getCarriageLine(carriage.getTempCarriageLineName()).addCarriage(carriage);
            } else
                addon.getPlugin().getLogger().warning("Could not load Carriage " + carriage.getName() + ": No Carriage-Line named " + carriage.getTempCarriageLineName() + " found");

            addon.getPlugin().getLogger().info("Loaded Carriage " + carriage.getName());
        }
    }

    public void saveAll() {
        for(String line : config.getKeys(false))
            config.set(line, null);

        carriageLines.values().forEach(carriageLine -> {
            config.set("carriageLines." + carriageLine.getName(), carriageLine);
            carriageLine.getCarriages().forEach(carriage -> config.set("carriages." + carriage.getName(), carriage));
        });
        config.save();
    }

    public FileLoader getConfig() {
        return config;
    }

    public CarriageLine getCarriageLine(String name) {
        return carriageLines.get(name);
    }

    public Set<String> getCarriageLineNames() {
        return carriageLines.keySet();
    }

}
