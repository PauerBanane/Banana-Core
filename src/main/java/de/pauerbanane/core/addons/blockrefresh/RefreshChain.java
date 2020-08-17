package de.pauerbanane.core.addons.blockrefresh;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.jumppads.Jumppad;
import de.pauerbanane.core.addons.jumppads.JumppadVector;
import de.pauerbanane.core.addons.jumppads.conditions.JumppadCondition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import javax.naming.ldap.PagedResultsControl;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("refreshchain")
public class RefreshChain implements ConfigurationSerializable {

    private boolean enabled;

    private ArrayList<Material> materials;

    public RefreshChain(boolean enabled, ArrayList<Material> materials) {
        if (materials == null || materials.size() == 0) return;
        this.materials = materials;
        this.enabled = enabled;
    }

    public RefreshChain(boolean enabled, Material material) {
        if (material == null) return;
        this.materials = Lists.newArrayList(material);
    }

    public boolean addMaterial(Material material) {
        if (hasMaterial(material)) return false;
        materials.add(material);
        return true;
    }

    public boolean removeMaterial(Material material) {
        if (!hasMaterial(material) || getFirst() == material) return false;
        materials.remove(material);
        return true;
    }

    public boolean hasMaterial(Material material) {
        return materials.contains(material);
    }

    public Material getFirst() {
        return materials.get(0);
    }

    public Material getLast() {
        if (materials.size() < 2) return Material.AIR;
        return materials.get(materials.size() - 1);
    }

    public Material getNext(Material material) {
        if (materials.size() < 2) return getLast();
        if (getLast() == material) return Material.AIR;

        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i) == material) {
                return materials.get(i + 1);
            }
        }
        return Material.AIR;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("enabled", enabled);

        for (int i = 0; i < materials.size(); i++) {
            result.put("materials." + i, materials.get(i).toString());
        }

        return result;
    }

    public static RefreshChain deserialize(Map<String, Object> args) {
        boolean enabled = (boolean) args.get("enabled");

        ArrayList<String> materialNames = Lists.newArrayList();
        args.keySet().stream().filter(arg -> arg.startsWith("materials")).forEach(arg -> materialNames.add((String) args.get(arg)));

        ArrayList<Material> materials = Lists.newArrayList();
        for (int i = 0; i < materialNames.size(); i++) {
            Material material = Material.getMaterial(materialNames.get(i));
            if (material != null)
                materials.add(material);
        }

        if (materials.size() == 0) {
            BananaCore.getInstance().getLogger().warning("Failed to load a RefreshChain with: " + materialNames);
            return null;
        }

        return new RefreshChain(enabled, materials);
    }

    public ArrayList<Material> getMaterials() {
        return materials;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
