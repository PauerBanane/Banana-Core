package de.pauerbanane.core.addons.discovery;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.discovery.commands.DiscoveryCommand;
import de.pauerbanane.core.addons.discovery.data.DiscoveryData;
import de.pauerbanane.core.addons.discovery.listener.DiscoveryListener;
import de.pauerbanane.core.addons.settings.data.Settings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashMap;

public class DiscoveryAddon extends Addon {

    private FileLoader config;

    private HashMap<String, Discovery> discoveries;

    private static DiscoveryAddon instance;

    @Override
    public void onEnable() {
        instance = this;
        this.discoveries = Maps.newHashMap();
        ConfigurationSerialization.registerClass(Discovery.class, "discovery");
        BananaCore.getInstance().getPlayerDataManager().registerPlayerData(plugin, DiscoveryData.class);
        load();
        commandSetup();

        registerListener(new DiscoveryListener(this));
    }

    public boolean registerDiscovery(Discovery discovery) {
        if (discovery == null) return false;
        if (discoveries.containsKey(discovery.getName())) return false;
        discoveries.put(discovery.getName(), discovery);
        return true;
    }

    public void unregisterDiscovery(Discovery discovery) {
        discoveries.remove(discovery.getName());
    }

    public boolean isDiscoveryRegion(String world, String regionID) {
        for (Discovery discovery : discoveries.values()) {
            if (discovery.getWorld().equals(world) && discovery.getRegionID().equals(regionID))
                return true;
        }
        return false;
    }

    private void load() {
        this.config = new FileLoader(getAddonFolder(), "Discovery.yml");
        int amount = 0;
        if (config.isSet("discoveries")) {
            ConfigurationSection section = config.getConfigurationSection("discoveries");
            for (String entry : section.getKeys(false)) {
                Discovery discovery = section.getSerializable(entry, Discovery.class);
                if (discovery == null || !registerDiscovery(discovery)) {
                    plugin.getLogger().warning("Failed to load discovery - Skipping");
                    continue;
                }
                amount++;
            }
        }

        plugin.getLogger().info("Loaded " + amount + " Discoveries");
    }

    private void commandSetup() {
        commandManager.getCommandCompletions().registerCompletion("discovery", c -> {
            return ImmutableList.copyOf(discoveries.keySet());
        });
        commandManager.getCommandContexts().registerContext(Discovery.class, c -> {
            final String tag = c.popFirstArg();
            Discovery discovery = getDiscovery(tag);
            if (discovery != null) {
                return discovery;
            } else
                throw new InvalidCommandArgument("Diese Discovery existiert nicht.");
        });

        registerCommand(new DiscoveryCommand(this));
    }

    public Discovery getDiscovery(String name) {
        return discoveries.get(name);
    }

    public Discovery getDiscovery(String world, String regionID) {
        for (Discovery discovery : discoveries.values()) {
            if (discovery.getWorld().equals(world) && discovery.getRegionID().equals(regionID))
                return discovery;
        }
        return null;
    }

    private void save() {
        if (config == null) return;
        config.set("discoveries", null);
        for (int i = 0; i < discoveries.values().size(); i++) {
            config.set("discoveries." + i, discoveries.values().toArray()[i]);
        }

        config.save();
    }

    @Override
    public void onDisable() {
        save();
    }

    @Override
    public void onReload() {
        save();
        load();
    }

    public static DiscoveryAddon getInstance() {
        return instance;
    }

    public HashMap<String, Discovery> getDiscoveries() {
        return discoveries;
    }
}
