package de.pauerbanane.core.util;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class UtilWorldGuard {

    private static WorldGuard api = WorldGuard.getInstance();

    private static RegionContainer regionContainer = api.getPlatform().getRegionContainer();

    public static RegionManager getRegionManager(World world) {
        return regionContainer.get(BukkitAdapter.adapt(world));
    }

    public static BlockVector3 getBlockVector3FromLocation(Location location) {
        return BlockVector3.at(location.getX(), location.getY(), location.getZ());
    }

    public static ProtectedRegion getRegion(Block block) {
        BlockVector3 bv3 = getBlockVector3FromLocation(block.getLocation());
       for (ProtectedRegion region : getRegionManager(block.getWorld()).getRegions().values()) {
           if (region.contains(bv3))
               return region;
       }

       return null;
    }

}
