package de.pauerbanane.core.addons.jumppads;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.api.util.WorldEditUtil;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.jumppads.conditions.AcidIslandLevelCondition;
import de.pauerbanane.core.addons.jumppads.conditions.JumppadCondition;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("jumppad")
public class Jumppad implements ConfigurationSerializable {

    private World world;

    private ProtectedRegion region;

    private Double defaultPower;

    private ArrayList<JumppadCondition> conditions;

    private JumppadVector mainVector;

    private JumppadVector targetVector;

    private Location targetLocation;

    public Jumppad(World world, ProtectedRegion region, double defaultPower, ArrayList<JumppadCondition> conditions, JumppadVector mainVector) {
        this.world = world;
        this.region = region;
        this.defaultPower = defaultPower;
        if (conditions != null) {
            this.conditions = conditions;
        } else
            this.conditions = Lists.newArrayList();
        this.mainVector = mainVector;
    }

    public boolean hasAchievedConditions(Player player) {
        for (JumppadCondition condition : conditions) {
            if (!condition.conditionAchieved(player))
                return false;
        }

        return true;
    }

    public Vector getFinalVector(Player player) {
        if (usePlayerDirection()) {
            return player.getEyeLocation().getDirection().clone().multiply(defaultPower);
        } else
            return mainVector.getFinalVector();
    }

    public void useJumppad(Player player) {
        player.setVelocity(getFinalVector(player));
        UtilPlayer.playSound(player, Sound.ENTITY_BLAZE_SHOOT);
        player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);

        if (targetVector != null) {
            Bukkit.getScheduler().runTaskLater(BananaCore.getInstance(), () -> {
                player.teleport(targetLocation);
                player.setVelocity(targetVector.getFinalVector());
                UtilPlayer.playSound(player, Sound.ENTITY_BLAZE_SHOOT);
                player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, 1);
            }, 30L);
        }
    }

    public void addCondition(JumppadCondition condition) {
        conditions.add(condition);
    }

    public void removeCondition(JumppadCondition condition) {
        conditions.remove(condition);
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("world", world.getName());
        result.put("region", region.getId());
        result.put("defaultPower", defaultPower);

        if (mainVector != null)
            result.put("mainVector", mainVector);

        if (targetLocation != null && targetVector != null) {
            result.put("targetVector", targetVector);
            result.put("targetLocation", UtilLoc.serialize(targetLocation));
        }

        for (int i = 0; i < conditions.size(); i++) {
            result.put("conditions." + i, conditions.get(i));
        }

        return result;
    }

    public static Jumppad deserialize(Map<String, Object> args) {
        World world = Bukkit.getWorld((String) args.get("world"));
        if (world == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load world" + args.get("world") + " - Not loading this Jumppad");
            return null;
        }
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion((String) args.get("region"));
        if (region == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load region" + args.get("region") + " - Not loading this Jumppad");
            return null;
        }

        double defaultPower = (double) args.get("defaultPower");

        JumppadVector mainVector = null;
        if (args.containsKey("mainVector"))
            mainVector = (JumppadVector) args.get("mainVector");

        ArrayList<JumppadCondition> jumppadConditions = Lists.newArrayList();
        args.keySet().stream().filter(arg -> arg.startsWith("conditions")).forEach(arg -> jumppadConditions.add((JumppadCondition) args.get(arg)));

        Jumppad jumppad = new Jumppad(world, region, defaultPower, jumppadConditions, mainVector);

        if (args.containsKey("targetVector") && args.containsKey("targetLocation")) {
            JumppadVector vector = (JumppadVector) args.get("targetVector");
            Location location = UtilLoc.deserialize((String) args.get("targetLocation"));
            if (location == null || location.getWorld() == null) {
                BananaCore.getInstance().getLogger().warning("Failed to load target location for jumppad - " + jumppad.getRegion().getId());
            } else {
                jumppad.setTargetLocation(location);
                jumppad.setTargetVector(vector);
            }
        }

        return jumppad;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public ArrayList<JumppadCondition> getConditions() {
        return conditions;
    }

    public boolean usePlayerDirection() {
        return mainVector == null;
    }

    public void setTargetVector(JumppadVector targetVector) {
        this.targetVector = targetVector;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    public void setDefaultPower(Double defaultPower) {
        this.defaultPower = defaultPower;
    }

    public JumppadVector getMainVector() {
        return mainVector;
    }

    public JumppadVector getTargetVector() {
        return targetVector;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public Double getDefaultPower() {
        return defaultPower;
    }

    public void setMainVector(JumppadVector mainVector) {
        this.mainVector = mainVector;
    }

    public World getWorld() {
        return world;
    }
}
