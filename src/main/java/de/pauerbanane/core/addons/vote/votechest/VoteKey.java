package de.pauerbanane.core.addons.vote.votechest;

import com.google.common.collect.Lists;
import com.mojang.datafixers.types.templates.List;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.addons.carriages.CarriageLine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("voteKey")
public class VoteKey implements ConfigurationSerializable {

    private static int commonItemsPerc = 90,
                       rareItemsPerc   = 99,
                       veryRareItemsPerc = 100;

    private VoteKey.Type type;

    private ArrayList<ItemStack> commonItems,
                                 rareItems,
                                 veryRareItems;

    public VoteKey(VoteKey.Type type, ArrayList<ItemStack> commonItems, ArrayList<ItemStack> rareItems, ArrayList<ItemStack> veryRareItems){
        this.type = type;
        this.commonItems = commonItems;
        this.rareItems = rareItems;
        this.veryRareItems = veryRareItems;
    }

    public ItemStack[] createInventory() {
        ItemStack[] contents = new ItemStack[13];
        for(int i = 0; i < 13; i++) {
            int random = UtilMath.random(0, 99);
            if(random < commonItemsPerc) {
                contents[i] = commonItems.get(UtilMath.random(0, commonItems.size() - 1));
            } else if (random < rareItemsPerc) {
                contents[i] = rareItems.get(UtilMath.random(0, rareItems.size() - 1));
            } else if (random < veryRareItemsPerc) {
                contents[i] = veryRareItems.get(UtilMath.random(0, veryRareItems.size() - 1));
            }
        }

        return contents;
    }

    public boolean containsRareItems(Inventory inventory) {
        for (ItemStack item : inventory.getContents())
            if (rareItems.contains(item))
                return true;

        return false;
    }

    public boolean containsVeryRareItems(Inventory inventory) {
        for (ItemStack item : inventory.getContents())
            if (veryRareItems.contains(item))
                return true;

        return false;
    }

    public ItemStack getVeryRareItem(Inventory inventory) {
        for (ItemStack item : inventory.getContents())
            if (veryRareItems.contains(item))
                return item.clone();

        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("type", type.toString());
        for (int i = 0; i < commonItems.size(); i++)
            result.put("commonItems." + i, commonItems.get(i));
        for (int i = 0; i < rareItems.size(); i++)
            result.put("rareItems." + i, rareItems.get(i));
        for (int i = 0; i < veryRareItems.size(); i++)
            result.put("veryRareItems." + i, veryRareItems.get(i));

        return result;
    }

    public static VoteKey deserialize(Map<String, Object> args) {
        Type type = getTypeByString((String) args.get("type"));
        ArrayList<ItemStack> commonItems    = Lists.newArrayList(),
                             rareItems      = Lists.newArrayList(),
                             veryRareItems  = Lists.newArrayList();

        args.keySet().stream().filter(arg -> arg.startsWith("commonItems")).forEach(arg -> commonItems.add((ItemStack) args.get(arg)));
        args.keySet().stream().filter(arg -> arg.startsWith("rareItems")).forEach(arg -> rareItems.add((ItemStack) args.get(arg)));
        args.keySet().stream().filter(arg -> arg.startsWith("veryRareItems")).forEach(arg -> veryRareItems.add((ItemStack) args.get(arg)));

        if (commonItems.isEmpty())      commonItems.add(new ItemStack(Material.WHEAT));
        if (rareItems.isEmpty())        rareItems.add(new ItemStack(Material.GOLD_INGOT));
        if (veryRareItems.isEmpty())    veryRareItems.add(new ItemStack(Material.DIAMOND));

        return new VoteKey(type, commonItems, rareItems, veryRareItems);
    }

    public enum Type {
        OLD_KEY,
        ANCIENT_KEY,
        EPIC_KEY;
    }

    public static VoteKey.Type getTypeByString(String s) {
        if(s.equalsIgnoreCase("OLD_KEY")) return Type.OLD_KEY;
        if(s.equalsIgnoreCase("ANICENT_KEY")) return Type.ANCIENT_KEY;
        if (s.equalsIgnoreCase("EPIC_KEY")) return Type.EPIC_KEY;
        return null;
    }

    public static String getVoteKeyName(VoteKey.Type type) {
        if (type == Type.OLD_KEY) return "§7Alte Truhe";
        if(type == Type.ANCIENT_KEY) return "§6Antike Truhe";
        if (type == Type.EPIC_KEY) return "§5Epische Truhe";
        return null;
    }

    public ArrayList<ItemStack> getCommonItems() {
        return commonItems;
    }

    public ArrayList<ItemStack> getRareItems() {
        return rareItems;
    }

    public ArrayList<ItemStack> getVeryRareItems() {
        return veryRareItems;
    }

    public Type getType() {
        return type;
    }
}
