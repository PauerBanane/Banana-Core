package de.pauerbanane.core.addons.regrowingTrees;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import de.pauerbanane.core.addons.plotshop.PlotGroup;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import de.pauerbanane.core.addons.portals.listener.PortalEnterListener;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("regrowingTree")
public class Tree implements ConfigurationSerializable {

    private String regionID;

    private ProtectedRegion region;

    private String world;

    private int taskID;

    private int seconds;

    private boolean isRunning;

    public Tree(String regionID, ProtectedRegion region, World world) {
        this.regionID = regionID;
        this.region = region;
        this.world = world.getName();
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public String getRegionID() {
        return regionID;
    }

    public String getWorld() {
        return world;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("regionID", this.regionID);
        result.put("world", this.world);
        return result;
    }

    public static Tree deserialize(Map<String, Object> args) {
        String id = (String) args.get("regionID");
        World world = Bukkit.getWorld((String) args.get("world"));
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(id);
        Tree tree = new Tree(id, region, world);

        return tree;
    }

    public void startCountdown() {
        this.seconds = RegrowingTrees.getInstance().getSeconds();
        if (isRunning) return;
        isRunning = true;

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {

            switch (seconds) {
                case 0:
                    RegrowingTrees.getInstance().getManager().resetTree(this);
                    stopCountdown();
                    break;
            }

            seconds--;
        },0 ,20);
    }

    public void stopCountdown() {
        if(!isRunning) return;
        isRunning = false;
        Bukkit.getScheduler().cancelTask(taskID);
    }

    public void forceRefresh() {
        if (isRunning) {
            stopCountdown();
            RegrowingTrees.getInstance().getManager().resetTree(this);
        }
    }
}
