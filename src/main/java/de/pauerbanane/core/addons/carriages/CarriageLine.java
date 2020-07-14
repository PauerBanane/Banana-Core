package de.pauerbanane.core.addons.carriages;

import com.google.common.collect.Lists;
import de.pauerbanane.api.util.UtilLoc;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("CarriageLine")
public class CarriageLine implements ConfigurationSerializable {

    private ArrayList<Carriage> carriages = Lists.newArrayList();

    private String world;

    private String name;

    public CarriageLine(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public boolean addCarriage(Carriage carriage) {
        if(carriage.hasCarriageLine()) return false;

        carriage.setCarriageLine(this);
        carriages.add(carriage);
        return carriages.add(carriage);
    }

    public String getName() {
        return name;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("world", world);
        for(int i = 0; i < carriages.size(); i++) {
            result.put("carriages." + (i+1), carriages.get(i));
        }

        return result;
    }

    public static CarriageLine deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        String world = (String) args.get("world");

        CarriageLine carriageLine = new CarriageLine(name, world);

        boolean idle = true;
        int i = 0;
        do {
            if((Carriage) args.get("carriages." + (i+1)) != null) {
                Carriage carriage = (Carriage) args.get("carriages." + (i+1));
                carriageLine.addCarriage(carriage);
            } else
                idle = false;
        } while(idle);

        return carriageLine;
    }
}
