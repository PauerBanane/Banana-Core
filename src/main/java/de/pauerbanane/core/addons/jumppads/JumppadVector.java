package de.pauerbanane.core.addons.jumppads;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.jumppads.conditions.JumppadCondition;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("jumppadvector")
public class JumppadVector implements ConfigurationSerializable {

    private Vector vector;

    private double power;

    public JumppadVector(Vector vector, double power) {
        this.vector = vector;
        this.power = power;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("vector", vector);
        result.put("power", power);

        return result;
    }

    public static JumppadVector deserialize(Map<String, Object> args) {
        Vector vector = (Vector) args.get("vector");
        double power = (double) args.get("power");

        return new JumppadVector(vector, power);
    }

    public Vector getFinalVector() {
        return vector.clone().multiply(power);
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public void setPower(double power) {
        this.power = power;
    }
}
