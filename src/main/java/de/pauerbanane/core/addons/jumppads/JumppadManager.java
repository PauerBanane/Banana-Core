package de.pauerbanane.core.addons.jumppads;

import com.comphenix.protocol.wrappers.nbt.io.NbtConfigurationSerializer;
import com.google.common.collect.Lists;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.jumppads.listener.JumppadListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.mozilla.javascript.ast.Jump;

import javax.security.auth.login.Configuration;
import java.util.ArrayList;

public class JumppadManager {

    private JumppadAddon addon;

    private ArrayList<Jumppad> jumppads;

    private FileLoader config;

    public JumppadManager(JumppadAddon addon) {
        this.addon = addon;
        this.jumppads = Lists.newArrayList();

        load();

        addon.registerListener(new JumppadListener(this));
    }

    private void load() {
        this.config = new FileLoader(addon.getAddonFolder(), "Jummppads.yml");
        if (config.isSet("jumppads")) {
            ConfigurationSection section = config.getConfigurationSection("jumppads");
            for (String entry : section.getKeys(false)) {
                Jumppad jumppad = section.getSerializable(entry, Jumppad.class);
                if (jumppad == null) {
                    BananaCore.getInstance().getLogger().warning("Failed to load Jumppad - Skipping");
                    continue;
                }
                registerJumppad(jumppad);
            }
        }
    }

    public void save() {
        config.set("jumppads", null);

        for (int i = 0; i < jumppads.size(); i++) {
            config.set("jumppads." + i, jumppads.get(i));
        }
        config.save();
    }

    public boolean registerJumppad(Jumppad jumppad) {
        if (isExisting(jumppad)) return false;
        jumppads.add(jumppad);
        return true;
    }

    public void removeJumppad(Jumppad jumppad) {
        this.jumppads.remove(jumppad);
    }

    public boolean isExisting(Jumppad jumppad) {
        for (Jumppad j : jumppads) {
            if (j.getWorld().getName().equals(jumppad.getWorld().getName()) && j.getRegion().getId().equals(jumppad.getRegion().getId()))
                return true;
        }
        return false;
    }

    public boolean isJumppadRegion(World world, ProtectedRegion region) {
        return getJumppad(world, region.getId()) != null;
    }

    public Jumppad getJumppad(World world, String regionID) {
        for (Jumppad jumppad : jumppads) {
            if (jumppad.getWorld().getName().equals(world.getName()) && jumppad.getRegion().getId().equals(regionID))
                return jumppad;
        }
        return null;
    }

    public ArrayList<String> getWorldJumppadRegions(World world) {
        ArrayList<String> jumppadRegions = Lists.newArrayList();
        jumppads.forEach(jumppad -> {
            if (jumppad.getWorld().getName().equals(world.getName()))
                jumppadRegions.add(jumppad.getRegion().getId());
        });
        return jumppadRegions;
    }

}
