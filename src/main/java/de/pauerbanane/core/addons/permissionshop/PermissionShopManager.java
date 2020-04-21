package de.pauerbanane.core.addons.permissionshop;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.MetaNode;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

public class PermissionShopManager {

    private LinkedHashMap<String, ShopNode> shopNodes;

    private PermissionShop addon;

    public PermissionShopManager(PermissionShop addon) {
        this.addon = addon;
        this.shopNodes = Maps.newLinkedHashMap();
        ShopNode.setManager(this);
    }

    public void load() {
        FileLoader config = addon.getConfig();

        for(String name : config.getKeys(false)) {
            ConfigurationSection section = config.getConfigurationSection(name);
            String displayName = section.getString("displayName");
            Material material = Material.getMaterial(section.getString(".icon", "PAPER"));
            if(material == null) {
                addon.getPlugin().getLogger().warning("No material matching " + section.getString("icon") + " found");
                continue;
            }
            int modelData = section.getInt(name + "modelData");
            List<String> lore = section.getStringList("lore");
            String condition = section.getString("condition");
            List<String> permissions = section.getStringList("permissions");
            String server = section.getString("server", addon.getPlugin().getServerName());
            String world = section.getString("world", "global");
            double price = section.getDouble("price");
            List<Node> permissionNodes = Lists.newArrayList();
            permissions.forEach(permission -> {
                if(permission.startsWith("meta:")) {
                    String perm = permission.replace("meta:", "");
                    String[] meta = perm.split(";");
                    String metaKey = meta[0];
                    String metaValue = meta[1];
                    MetaNode metaNode = PermissionManager.buildMetaNode(metaKey, metaValue, server, world);
                    permissionNodes.add(metaNode);
                } else
                    permissionNodes.add(PermissionManager.buildPermissionNode(permission, true, server, world));
            });

            shopNodes.put(name, new ShopNode(name, displayName, material, modelData, lore, price, condition, permissionNodes));
        }
        addon.getPlugin().getLogger().info("Loaded " + shopNodes.size() + " Permission-Shop entries");
    }

    public Collection<ShopNode> getNodes() {
        return shopNodes.values();
    }

    public ShopNode getShopNode(String name) {
        return shopNodes.get(name);
    }

    public boolean isShopNode(String name) {
        return shopNodes.containsKey(name);
    }

    public PermissionShop getAddon() {
        return addon;
    }
}
