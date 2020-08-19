package de.pauerbanane.core.addons.discovery.data;

import com.google.common.collect.Lists;
import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.discovery.Discovery;
import de.pauerbanane.core.addons.discovery.DiscoveryAddon;

import org.bukkit.NamespacedKey;

import org.bukkit.configuration.file.YamlConfiguration;


import java.util.ArrayList;
import java.util.List;

public class DiscoveryData extends PlayerData {

    private static NamespacedKey key = new NamespacedKey(BananaCore.getInstance(), "discoveryData");

    private ArrayList<Discovery> unlockedDiscoveries = Lists.newArrayList();

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration config) {
        config.set(key.getKey(), null);

        List<String> discoveryNames = Lists.newArrayList();
        unlockedDiscoveries.forEach(discovery -> discoveryNames.add(discovery.getName()));

        config.set(key.getKey(), discoveryNames);
    }

    @Override
    public void loadData(YamlConfiguration config) {
        DiscoveryAddon addon = DiscoveryAddon.getInstance();
        if (addon == null) return;

        if (!config.isSet(key.getKey())) return;
        List<String> discoveryNames = config.getStringList(key.getKey());

        discoveryNames.forEach(discoveryName -> {
            if (addon.getDiscovery(discoveryName) != null && !unlockedDiscoveries.contains(addon.getDiscovery(discoveryName)))
                unlockedDiscoveries.add(addon.getDiscovery(discoveryName));
        });
    }

    public boolean hasAchievedDiscovery(Discovery discovery) {
        return unlockedDiscoveries.contains(discovery);
    }

    public void addDiscovery(Discovery discovery) {
        unlockedDiscoveries.add(discovery);
    }

    public void removeDiscovery(Discovery discovery) {
        unlockedDiscoveries.remove(discovery);
    }

    public int getTotalUnlockedDiscoveries() {
        return unlockedDiscoveries.size();
    }

}
