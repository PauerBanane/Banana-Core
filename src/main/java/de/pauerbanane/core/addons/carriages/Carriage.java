package de.pauerbanane.core.addons.carriages;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.UtilLoc;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Carriage")
public class Carriage implements ConfigurationSerializable {

    private String name;

    private String regionID;

    private Location targetLocation;

    private CarriageLine carriageLine;

    public Carriage(String name, String regionID, Location targetLocation) {
        this.name = name;
        this.regionID = regionID;
        this.targetLocation = targetLocation;
    }

    public boolean hasCarriageLine() {
        return carriageLine == null;
    }

    public void setCarriageLine(CarriageLine carriageLine) {
        this.carriageLine = carriageLine;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("name", name);
        result.put("location", UtilLoc.serialize(targetLocation));
        result.put("regionID", regionID);
        result.put("line", carriageLine.getName());

        return result;
    }

    public static Carriage deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        String regionID = (String) args.get("regionID");
        Location targetLocation = UtilLoc.deserialize((String) args.get("location"));

        return new Carriage(name, regionID, targetLocation);
    }

}
