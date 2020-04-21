package de.pauerbanane.core.addons.portals;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.portals.commands.PortalCommand;
import de.pauerbanane.core.addons.portals.listener.PortalEnterListener;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Portals extends Addon {

    private File file;

    private HashMap<String, Portal> portals;

    @Override
    public void onEnable() {
        this.file = new File(getAddonFolder() + "Portals.yml");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.portals = Maps.newHashMap();

        ConfigurationSerialization.registerClass(Portal.class);
        load();
        commandSetup();

        registerCommand(new PortalCommand(this));

        registerListener(new PortalEnterListener(this));
    }

    public void save() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("portals", null);
        int i = 0;
        for(Portal portal : portals.values()) {
            config.set("portals." + i, portal);
            i++;
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("Saved " + portals.size() + " portals");
    }

    private void commandSetup() {
        commandManager.getCommandContexts().registerContext(Portals.PortalType.class, c -> {
            final String tag = c.popFirstArg();
            PortalType portalType = PortalType.valueOf(tag.toUpperCase());
            if(portalType != null) {
                return portalType;
            } else
                throw new InvalidCommandArgument("Invalid PortalType specified.");
        });
        commandManager.getCommandContexts().registerContext(Portal.class, c -> {
            final String tag = c.popFirstArg();
            Portal portal = getPortal(tag);
            if(portal != null) {
                return portal;
            } else
                throw new InvalidCommandArgument("Invalid Portal specified.");
        });

        commandManager.getCommandCompletions().registerCompletion("portaltype", c -> ImmutableList.of("worldportal", "serverportal"));
        commandManager.getCommandCompletions().registerCompletion("portal", c -> ImmutableList.copyOf(portals.keySet()));
    }

    private void load() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("portals");
        if(section == null) return;
        for(String key : section.getKeys(false)) {
            Portal portal = config.getSerializable("portals." + key, Portal.class);
            registerPortal(portal);
        }
        plugin.getLogger().info("Loaded " + portals.size() + " portals");
    }

    public Portal getPortalByRegion(World world, String regionID) {
        for(Portal portal : portals.values()) {
            if(portal.getPortalWorld().getName().equals(world.getName()) && portal.getPortalRegion().equals(regionID))
                return portal;
        }
        return null;
    }

    public boolean isPortalRegion(World world, String regionID) {
        for(Portal portal : portals.values()) {
            if(portal.getPortalWorld().getName().equals(world.getName()) && portal.getPortalRegion().equals(regionID))
                return true;
        }
        return false;
    }

    public boolean registerPortal(Portal portal) {
        for(Portal p : portals.values()) {
            if(portal.getName().equals(p.getName()) || (portal.getPortalWorld().getName().equals(p.getPortalWorld().getName()) && portal.getPortalRegion().equals(p.getPortalRegion())))
                return false;
        }
        portals.put(portal.getName(), portal);
        return true;
    }

    public boolean removePortal(Portal portal) {
        if(!portals.containsKey(portal.getName()))
            return false;
        portals.remove(portal.getName());
        return true;
    }

    public Portal getPortal(String name) {
        return portals.get(name);
    }

    @Override
    public void onDisable() {
        save();
    }

    public HashMap<String, Portal> getPortals() {
        return portals;
    }

    public enum PortalType {
        WORLDPORTAL, SERVERPORTAL;
    }
}
