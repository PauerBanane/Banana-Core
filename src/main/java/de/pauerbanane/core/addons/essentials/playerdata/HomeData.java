package de.pauerbanane.core.addons.essentials.playerdata;

import com.google.common.collect.Maps;
import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class HomeData extends PlayerData {

    private static final NamespacedKey key = new NamespacedKey(BananaCore.getInstance(), "homes");

    private final HashMap<String, Location> homes = Maps.newHashMap();

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration config) {
        config.set(key.getKey(), null);
        homes.keySet().forEach(homeName -> {
            if (homes.get(homeName).getWorld() != null)
                config.set(key.getKey() + "." + homeName, UtilLoc.serialize(homes.get(homeName)));
        });
    }

    @Override
    public void loadData(YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(key.getKey());
        if(section == null)
            return;

        section.getKeys(false).forEach(homeName -> {
            Location location = UtilLoc.deserialize(section.getString(homeName));
            if (location != null) {
                homes.put(homeName, location);
            } else
                Bukkit.getPlayer(getOwner()).sendMessage(F.error("Homes", "Der Homepunkt §e" + homeName + " §7konnte nicht geladen werden, da die Welt nicht existiert."));
        });
        int currentLimit = getMaxHomes();
        if (homes.size() > getMaxHomes()) {
            Node node = PermissionManager.buildMetaNode("homelimit", String.valueOf(homes.size()), BananaCore.getInstance().getServerName(), null);
            PermissionManager.addNode(Bukkit.getPlayer(this.getOwner()), node);
        };
    }

    public boolean addHome(String name, Location location) {
        if(homes.containsKey(name)) {
            homes.put(name, location);
            return true;
        }

        if(homes.size() >= getMaxHomes())
            return false;
        homes.put(name, location);
        return true;
    }

    public boolean deleteHome(String name) {
        if (this.homes.containsKey(name)) {
            this.homes.remove(name);
            getConfig().set(String.valueOf(key.getKey()) + "." + name, null);
            return true;
        }
        return false;
    }

    public int getMaxHomes() {
        String metaValue = PermissionManager.getMetaValue(getOwner(), "homelimit", BananaCore.getInstance().getServerName(), null);

        return UtilMath.isInt(metaValue) ? Integer.valueOf(metaValue) : 1;
    }

    public boolean hasHome(String name) {
        return this.homes.containsKey(name);
    }

    public int getTotalHomes() {
        return homes.size();
    }

    public Location getHome(String homeName) {
        return homes.get(homeName);
    }

    public HashMap<String, Location> getHomes() {
        return homes;
    }
}
