package de.pauerbanane.core.addons.regrowingTrees;

import com.google.common.collect.Maps;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.api.util.WorldEditUtil;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class TreeManager {

    private RegrowingTrees addon;

    private File treeFolder;

    private File schematicFolder;

    private HashMap<String, Tree> regrowingTrees = Maps.newHashMap();

    public TreeManager(RegrowingTrees addon) {
        this.addon = addon;
        this.treeFolder = new File(addon.getPlugin().getDataFolder(), "RegrowingTrees");
        if (!treeFolder.exists())
             treeFolder.mkdirs();
        this.schematicFolder = new File(treeFolder, "schematics");
        if (!schematicFolder.exists())
             schematicFolder.mkdirs();

        ConfigurationSerialization.registerClass(Tree.class, "regrowingTree");

        load();
    }

    public void shutdown() {
        regrowingTrees.values().forEach(tree -> tree.forceRefresh());
    }

    public void resetTree(Tree tree) {
        CuboidRegion cuboidRegion = new CuboidRegion(tree.getRegion().getMinimumPoint(), tree.getRegion().getMaximumPoint());
        WorldEditUtil.restoreRegionBlocks(new File(schematicFolder, tree.getRegionID() + ".schem"), cuboidRegion, Bukkit.getWorld(tree.getWorld()));
    }

    public Tree treeChopped(Block block) {
        String world = block.getWorld().getName();
        BlockVector3 bv3 = BlockVector3.at(block.getX(), block.getY(), block.getZ());
        for (Tree tree : regrowingTrees.values()) {
            if (tree.getWorld().equals(world) && tree.getRegion().contains(bv3))
                return tree;
        }
        return null;
    }

    private void load() {
        int treeAmount = 0;
        Iterator<File> trees = FileUtils.iterateFiles(this.treeFolder, new String[] {"yml"}, false);
        while (trees.hasNext()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(trees.next());
            Tree tree = config.getSerializable("regrowingTree", Tree.class);
            this.regrowingTrees.put(tree.getRegionID(), tree);
            treeAmount++;
        }
        addon.getPlugin().getLogger().info("Loaded " + treeAmount + " Regrowing-Trees.");
    }

    public Collection<Tree> getTreeMap() {
        return this.regrowingTrees.values();
    }

    public Tree getTree(String regionID) {
        return this.regrowingTrees.get(regionID);
    }

    public void saveTree(Tree tree) {
        File file = new File(this.treeFolder, tree.getRegionID() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("regrowingTree", tree);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteTree(Tree tree) {
        this.regrowingTrees.remove(tree.getRegionID());
        File file = new File(treeFolder, tree.getRegionID() + ".yml");
        file.delete();
        File schematic = new File(schematicFolder, String.valueOf(tree.getRegionID()) + ".schem");
        schematic.delete();
    }

    public void registerTree(Tree tree) throws IOException {
        CuboidRegion cuboidRegion = new CuboidRegion(tree.getRegion().getMinimumPoint(), tree.getRegion().getMaximumPoint());
        WorldEditUtil.saveRegionBlocks(new File(this.schematicFolder, tree.getRegionID() + ".schem"), (Region)cuboidRegion, Bukkit.getWorld(tree.getWorld()));
        this.regrowingTrees.put(tree.getRegionID(), tree);
        saveTree(tree);
    }

}
