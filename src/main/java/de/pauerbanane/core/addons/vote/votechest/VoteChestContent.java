package de.pauerbanane.core.addons.vote.votechest;

import com.google.common.collect.Lists;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("votechestcontent")
public class VoteChestContent implements ConfigurationSerializable {

    private ArrayList<ItemStack> contents;

    private Type type;

    public VoteChestContent(ArrayList<ItemStack> contents, Type type) {
        if (contents != null) {
            this.contents = contents;
        } else
            this.contents = Lists.newArrayList();
        this.type = type;
    }

    public String getDisplayName() {
        if (type == Type.COMMON) {
            return String.valueOf("§8Gewöhnliche §7Items");
        } else if (type == Type.RARE) {
            return String.valueOf("§6Seltene §7Items");
        } else if (type == Type.EPIC) {
            return String.valueOf("§dEpische §7Items");
        } else
            return String.valueOf("§8Unbekannte Items");
    }

    public void addContent(ItemStack item) {
        if (this.contents.size() >= 27) return;
        this.contents.add(item);
    }

    public void removeContent(ItemStack item) {
        ItemStack toRemove = null;
        for (ItemStack i : contents) {
            if (i.equals(item))
                toRemove = i;
        }

        if (toRemove != null)
            contents.remove(toRemove);
    }

    public ItemStack getRandomItem() {
        int random = UtilMath.random(0, contents.size() - 1);
        return contents.get(random).clone();
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("type", type.toString().toLowerCase());

        for (int i = 0; i < contents.size(); i++) {
            result.put("items." + i, contents.get(i));
        }

        return result;
    }

    public static VoteChestContent deserialize(Map<String, Object> args) {
        Type type = null;
        String typeName = (String) args.get("type");
        for (Type t : Type.values()) {
            if (t.toString().toLowerCase().equals(typeName))
                type = t;
        }
        if (type == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load VoteChest-Content with Type: " + typeName);
            return null;
        }

        ArrayList<ItemStack> items = Lists.newArrayList();
        args.keySet().stream().filter(arg -> arg.startsWith("items")).forEach(arg -> items.add((ItemStack) args.get(arg)));


        VoteChestContent voteChestContent = new VoteChestContent(items, type);
        return voteChestContent;
    }

    public enum Type {
        COMMON, RARE, EPIC;
    }

    public ArrayList<ItemStack> getContents() {
        return contents;
    }

    public Type getType() {
        return type;
    }
}
