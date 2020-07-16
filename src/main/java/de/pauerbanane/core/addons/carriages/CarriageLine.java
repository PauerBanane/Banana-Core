package de.pauerbanane.core.addons.carriages;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilLoc;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("CarriageLine")
public class CarriageLine implements ConfigurationSerializable {

    private HashMap<String, Carriage> carriages = Maps.newHashMap();

    private String world;

    private String name;

    public CarriageLine(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public boolean addCarriage(Carriage carriage) {
        if(carriage.hasCarriageLine()) return false;

        carriage.setCarriageLine(this);
        carriages.put(carriage.getName(), carriage);
        return true;
    }

    public boolean removeCarriage(Carriage carriage) {
        if(!carriages.containsValue(carriage)) return false;
        carriages.remove(carriage.getName());
        return true;
    }

    public boolean hasCarriage(String name) {
        return carriages.containsKey(name);
    }

    public Carriage getCarriage(String name) {
        if(!hasCarriage(name)) return null;
        return carriages.get(name);
    }

    public Set<String> getCarriageNames() {
        return carriages.keySet();
    }

    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("world", world);

        return result;
    }

    public static CarriageLine deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        String world = (String) args.get("world");

        CarriageLine carriageLine = new CarriageLine(name, world);

        return carriageLine;
    }

    public String getWorld() {
        return world;
    }

    public Collection<Carriage> getCarriages() {
        return carriages.values();
    }
}
