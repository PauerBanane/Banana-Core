package de.pauerbanane.core.addons.snowcontrol;

import com.google.common.collect.Lists;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import java.util.ArrayList;
import java.util.List;

public class SnowControl extends Addon implements Listener {

    private ArrayList<Material> disabledBlocks;

    @Override
    public void onEnable() {
        this.disabledBlocks = Lists.newArrayList();
        initConfig();

        registerListener(this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    private void initConfig() {
        FileLoader config = new FileLoader(getAddonFolder(), "SnowControl.yml");
        if(!config.isSet("disabledBlocks")) {
            config.set("disabledBlocks", Lists.newArrayList("GRAVEL"));
            config.save();
        }

        List<String> blockedMaterials = config.getStringList("disabledBlocks");
        for(String mat : blockedMaterials) {
            if(Material.getMaterial(mat) == null) {
                plugin.getLogger().warning("Failed to load Material '" + mat + "' from " + config.getFile().getPath() + ": The material does not exist");
            } else {
                disabledBlocks.add(Material.getMaterial(mat));
            }
        }

        plugin.getLogger().info("Loaded " + disabledBlocks.size() + " blocks for SnowControl");
    }

    @EventHandler
    public void handleSnow(BlockFormEvent e) {
        if(!(e.getBlock().getType() == Material.AIR)) return;
        Location loc = e.getBlock().getLocation().clone().subtract(0,1,0);
        Block block = loc.getBlock();
        if(block == null) return;
        if(disabledBlocks.contains(block.getType())) {
            e.setCancelled(true);
        }
    }

}
