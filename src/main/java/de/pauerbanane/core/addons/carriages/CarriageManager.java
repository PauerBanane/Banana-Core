package de.pauerbanane.core.addons.carriages;

import com.google.common.collect.Maps;
import de.pauerbanane.api.util.FileLoader;
import org.bukkit.World;

import java.util.HashMap;

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
        if(getCarriageLine(name) == null) return false;
        CarriageLine carriageLine = new CarriageLine(name, world.getName());
        carriageLines.put(carriageLine.getName(), carriageLine);
        return true;
    }

    public boolean removeLine(CarriageLine line) {
        if(!carriageLines.values().contains(line)) return false;
        carriageLines.remove(line.getName());
        return true;
    }

    private void load() {
        for (String line : config.getKeys(false)) {
            CarriageLine carriageLine = config.getConfig().getSerializable(line, CarriageLine.class);
            carriageLines.put(carriageLine.getName(), carriageLine);
            addon.getPlugin().getLogger().info("Loaded Carriage-Line " + carriageLine.getName());
        }
        addon.getPlugin().getLogger().info("Loaded " + carriageLines.size() + " Carriage-Lines");
    }

    private void save() {
        for(String line : config.getKeys(false))
            config.set(line, null);

        carriageLines.values().forEach(carriageLine -> config.set(carriageLine.getName(), carriageLine));
    }

    public CarriageLine getCarriageLine(String name) {
        return carriageLines.get(name);
    }

}
