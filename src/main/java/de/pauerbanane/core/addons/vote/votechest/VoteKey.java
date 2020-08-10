package de.pauerbanane.core.addons.vote.votechest;

import com.google.common.collect.Lists;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("votekey")
public class VoteKey implements ConfigurationSerializable {

    private String name;

    private String displayName;

    private Material icon;

    private int modelData;

    private int requiredVotes;

    private ArrayList<VoteChestContent> chestContents;

    private static int commonItemsPerc = 90,
                       rareItemsPerc   = 99,
                       epicItemsPerc = 100;

    public VoteKey(String name, String displayName, Material icon, int modelData, ArrayList<VoteChestContent> chestContents, int requiredVotes) {
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.modelData = modelData;
        if (chestContents != null) {
            this.chestContents = chestContents;
        } else
            this.chestContents = Lists.newArrayList(new VoteChestContent(null, VoteChestContent.Type.COMMON),
                                                    new VoteChestContent(null, VoteChestContent.Type.RARE),
                                                    new VoteChestContent(null, VoteChestContent.Type.EPIC));

        if (requiredVotes <= 0) {
            this.requiredVotes = 10000;
        } else
            this.requiredVotes = requiredVotes;
    }

    public ItemStack getDescriptionItem(Player player) {
        VoteData voteData = CorePlayer.get(player.getUniqueId()).getData(VoteData.class);
        int obtainedKeys = voteData.getVoteKeys(this);

        ItemBuilder builder = new ItemBuilder(icon).name(displayName).setModelData(modelData);

        builder.lore("");
        builder.lore("§7Du besitzt §e" + obtainedKeys + " §7Schlüssel.");
        builder.lore("");
        if (player.hasPermission("command.votechest"))
            builder.lore("§2Rechtsklick §7um den Inhalt zu bearbeiten.");

        return builder.build();
    }

    public ItemStack[] createInventory() {
        ItemStack[] contents = new ItemStack[13];
        for(int i = 0; i < 13; i++) {
            int random = UtilMath.random(0, 99);
            if(random < commonItemsPerc && hasContentType(VoteChestContent.Type.COMMON)) {
                contents[i] = getChestContent(VoteChestContent.Type.COMMON).getRandomItem();
            } else if (random < rareItemsPerc && hasContentType(VoteChestContent.Type.RARE)) {
                contents[i] = getChestContent(VoteChestContent.Type.RARE).getRandomItem();
            } else if (random < epicItemsPerc && hasContentType(VoteChestContent.Type.EPIC)) {
                contents[i] = getChestContent(VoteChestContent.Type.EPIC).getRandomItem();
            }
        }

        return contents;
    }

    public VoteChestContent getChestContent(VoteChestContent.Type type) {
        for (VoteChestContent c : chestContents) {
            if (c.getType() == type)
                return c;
        }

        VoteChestContent c = new VoteChestContent(null, type);
        addContent(c);
        return c;
    }

    public ItemStack getEpicItem(Inventory inventory) {
        if (!hasContentType(VoteChestContent.Type.EPIC)) return null;
        for (ItemStack item : inventory.getContents())
            if (getChestContent(VoteChestContent.Type.EPIC).getContents().contains(item))
                return item.clone();

        return null;
    }

    public boolean hasContentType(VoteChestContent.Type type) {
        for (VoteChestContent chestContent : chestContents) {
            if (chestContent.getType() == type)
                return true;
        }

        return false;
    }

    public boolean addContent(VoteChestContent content) {
        if (hasContentType(content.getType())) return false;
        chestContents.add(content);
        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("name", name);
        result.put("displayName", displayName);
        result.put("icon", icon.toString());
        result.put("modelData", modelData);
        result.put("requiredVotes", requiredVotes);

        for (int i = 0; i < chestContents.size(); i++) {
            result.put("chestContents." + i, chestContents.get(i));
        }

        return result;
    }

    public static VoteKey deserialize(Map<String, Object> args) {
        String name = (String) args.get("name");
        String displayName = (String) args.get("displayName");
        String materialName = (String) args.get("icon");
        Material material = Material.valueOf(materialName);
        if (material == null) {
            material = Material.PAPER;
            BananaCore.getInstance().getLogger().warning("Failed to load Material " + materialName + " - Resetting to PAPER");
        }
        int modelData = (int) args.get("modelData");
        int requiredVotes = (int) args.getOrDefault("requiredVotes", 0);

        ArrayList<VoteChestContent> contents = Lists.newArrayList();
        args.keySet().stream().filter(arg -> arg.startsWith("chestContents")).forEach(arg -> contents.add((VoteChestContent) args.get(arg)));

        VoteKey voteKey = new VoteKey(name,displayName, material, modelData, contents, requiredVotes);
        return voteKey;
    }

    public Material getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public ArrayList<VoteChestContent> getChestContents() {
        return chestContents;
    }

    public int getModelData() {
        return modelData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setModelData(int modelData) {
        this.modelData = modelData;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public int getRequiredVotes() {
        return requiredVotes;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setRequiredVotes(int requiredVotes) {
        this.requiredVotes = requiredVotes;
    }
}
