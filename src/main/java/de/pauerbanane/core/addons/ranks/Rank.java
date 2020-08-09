package de.pauerbanane.core.addons.ranks;

import com.google.common.collect.Lists;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.model.group.Group;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("rank")
public class Rank implements ConfigurationSerializable {

    private Group group;

    private ArrayList<RankCondition> conditions;

    private Material icon;

    public Rank( Group group, Material icon, ArrayList<RankCondition> conditions) {
        this.group = group;
        this.icon = icon;
        if (conditions != null) {
            this.conditions = conditions;
        } else
            this.conditions = Lists.newArrayList();
    }

    public void addCondition(RankCondition condition) {
        conditions.add(condition);
    }

    public void removeCondition(RankCondition condition) {
        conditions.remove(condition);
    }

    public boolean hasRank(Player player) {
        return player.hasPermission("group." + group.getName());
    }

    public ItemStack getDescriptionItem(Player player) {
        boolean obtained = hasRank(player);

        ItemBuilder builder = new ItemBuilder(icon).name(group.getDisplayName()).lore("");

        if (obtained) {
            builder.lore("§2Bereits freigeschalten");
        } else {
            if (hasAchievedConditions(player)) {
                builder.lore("§7Voraussetzungen §8- §2Erfüllt");
            } else
                builder.lore("§7Voraussetzungen §8- §4Nicht erfüllt");
            conditions.forEach(c -> builder.lore(c.requirementsAsLore(player)));
        }

        builder.lore("");

        return builder.build();
    }

    public void uprank(Player player) {
        if (hasAchievedConditions(player))
            PermissionManager.getUser(player.getUniqueId()).setPrimaryGroup(group.getName());
    }

    public boolean hasAchievedConditions(Player player) {
        for (RankCondition condition : conditions) {
            if (!condition.conditionAchieved(player))
                return false;
        }

        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("group", group.getName());

        for (int i = 0; i < conditions.size(); i++) {
            result.put("conditions." + i, conditions.get(i));
        }

        result.put("icon", icon.toString());

        return result;
    }

    public static Rank deserialize(Map<String, Object> args) {
        Group group = PermissionManager.getApi().getGroupManager().getGroup((String) args.get("group"));
        Rank requiredRank = null;
        Material icon = Material.valueOf((String) args.get("icon"));
        ArrayList<RankCondition> rankConditions = Lists.newArrayList();
        args.keySet().stream().filter(arg -> arg.startsWith("conditions")).forEach(arg -> rankConditions.add((RankCondition) args.get(arg)));

        if (group == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load Group " + group.getName() + ": Invalid LuckPerms Group");
            return null;
        }


        Rank rank = new Rank(group, icon, rankConditions);
        return rank;
    }

    public ArrayList<RankCondition> getConditions() {
        return conditions;
    }

    public Group getGroup() {
        return group;
    }

    public Material getIcon() {
        return icon;
    }

    public List<String> getRequirementsLore(Player player) {
        List<String> lore = Lists.newArrayList("§6Voraussetzungen:");
        conditions.forEach(c -> lore.addAll(c.requirementsAsLore(player)));
        lore.add("");
        lore.add(hasAchievedConditions(player) ? "§2Erledigt" : "§4Nicht erledigt");

        return lore;
    }
}
