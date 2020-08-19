package de.pauerbanane.core.addons.discovery;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.discovery.data.DiscoveryData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("discovery")
public class Discovery implements ConfigurationSerializable {

    private String regionID;

    private String world;

    private String name;

    private Sound sound;

    private Material icon;

    private List<String> lore;

    public Discovery(String name, String world, String regionID, Material icon, List<String> lore) {
        this.name = name;
        this.world = world;
        this.regionID = regionID;
        this.sound = Sound.UI_TOAST_CHALLENGE_COMPLETE;
        if (icon == null) {
            this.icon = Material.RED_WOOL;
        } else
            this.icon = icon;
        if (lore == null) {
            this.lore = Lists.newArrayList();
        } else
            this.lore = lore;
    }

    public void setSound(Sound sound) {
        this.sound = sound;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("name", name);
        result.put("regionID", regionID);
        result.put("world", world);
        result.put("sound", sound.toString());
        result.put("icon", icon.toString());
        result.put("lore", lore);

        return result;
    }

    public static Discovery deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");

        World world = Bukkit.getWorld((String) args.get("world"));
        if (world == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load world" + args.get("world") + " - Not loading this Discovery");
            return null;
        }
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion((String) args.get("regionID"));
        if (region == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load region" + args.get("region") + " - Not loading this Discovery");
            return null;
        }

        List<String> lore = (List<String>) args.get("lore");
        Material material = Material.getMaterial((String) args.get("icon"));
        if (material == null)
            material = Material.RED_WOOL;

        Discovery discovery = new Discovery(name, world.getName(), region.getId(), material, lore);
        discovery.setSound(Sound.valueOf((String) args.get("sound")));

        return discovery;
    }

    public List<String> getLore(Player player) {
        DiscoveryData data = CorePlayer.get(player.getUniqueId()).getData(DiscoveryData.class);
        if (data.hasAchievedDiscovery(this)) {
            return Lists.newArrayList("§cUnbekannt");
        } else
            return lore;

    }

    public ItemStack getDisplayItem(Player player) {
        DiscoveryData data = CorePlayer.get(player.getUniqueId()).getData(DiscoveryData.class);
        String displayName = data.hasAchievedDiscovery(this) ? name : "§e???";
        return new ItemBuilder(icon).name(displayName).lore(getLore(player)).clearEnchantment().build();
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public String getRegionID() {
        return regionID;
    }

    public Sound getSound() {
        return sound;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }
}
