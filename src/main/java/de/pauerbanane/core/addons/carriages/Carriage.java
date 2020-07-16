package de.pauerbanane.core.addons.carriages;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilLoc;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("Carriage")
public class Carriage implements ConfigurationSerializable {

    private String tempCarriageLineName;

    private String name;

    private String regionID;

    private Location targetLocation;

    private CarriageLine carriageLine;

    private Material material;

    public Carriage(String name, String regionID, Location targetLocation, Material material) {
        this.name = name;
        this.regionID = regionID;
        this.targetLocation = targetLocation;
        this.material = material;
    }

    public ItemStack getItem() {
        return new ItemBuilder(material).name(name).build();
    }

    public boolean hasCarriageLine() {
        return carriageLine != null;
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
        result.put("material", material.toString());
        result.put("carriageLine", carriageLine.getName());

        return result;
    }

    public static Carriage deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        String regionID = (String) args.get("regionID");
        Location targetLocation = UtilLoc.deserialize((String) args.get("location"));
        Material material = Material.getMaterial((String) args.get("material"));

        Carriage carriage = new Carriage(name,regionID,targetLocation,material);
        carriage.setTempCarriageLineName((String) args.get("carriageLine"));

        return carriage;
    }

    public String getName() {
        return name;
    }

    public CarriageLine getCarriageLine() {
        return carriageLine;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Material getMaterial() {
        return material;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setTempCarriageLineName(String tempCarriageLineName) {
        this.tempCarriageLineName = tempCarriageLineName;
    }

    public String getTempCarriageLineName() {
        return tempCarriageLineName;
    }
}
