package de.pauerbanane.core.addons.permissionshop;

import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.node.Node;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;

public class ShopNode {

    private static PermissionShopManager manager;

    private String name;

    private String displayName;

    private Material material;

    private int modelData;

    private List<String> lore;

    private double price;

    private String condition;

    private List<Node> permissionsNodes;

    public ShopNode(String name, String displayName, Material material, int modelData, List<String> lore, double price, String condition, List<Node> permissionNodes) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.modelData = modelData;
        this.lore = lore;
        this.price = price;
        this.condition = condition;
        this. permissionsNodes = permissionNodes;
    }

    public ShopNode getConditionNode() {
        return manager.getShopNode(condition);
    }

    public boolean hasCondition(Player player) {
        if(condition == null)
            return true;

        ShopNode conditionShopNode = manager.getShopNode(condition);

        if(conditionShopNode == null){
            manager.getAddon().getPlugin().getLogger().warning("Unknown condition set for ShopNode " + displayName);
            return false;
        }

        return conditionShopNode.hasBought(player);
    }

    public void addPermissions(Player player) {
        permissionsNodes.forEach(node -> PermissionManager.addNode(player, node));

    }

    public boolean hasBought(Player player) {
        if(permissionsNodes == null) return true;

        for(Node node : permissionsNodes) {
            if(!PermissionManager.hasNode(player.getUniqueId(), node))
                return false;
        }

        return true;
    }

    public static void setManager(PermissionShopManager manager) {
        ShopNode.manager = manager;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public int getModelData() {
        return modelData;
    }

    public List<String> getLore() {
        return lore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getCondition() {
        return condition;
    }

    public String getDisplayName() {
        return displayName;
    }
}
