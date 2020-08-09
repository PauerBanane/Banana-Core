package de.pauerbanane.core.addons.ranks.conditions;

import com.google.common.collect.Lists;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.model.group.Group;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("groupCondition")
public class GroupCondition extends RankCondition {

    private Group requiredGroup;

    public GroupCondition(Group requiredGroup) {
        super.type = TYPE.GROUP_CONDITION;
        this.requiredGroup = requiredGroup;
    }


    @Override
    public boolean conditionAchieved(Player player) {
        return player.hasPermission("group." + requiredGroup.getName());
    }

    @Override
    public List<String> requirementsAsLore(Player player) {
        List<String> lore = Lists.newArrayList();
        lore.add("§7Rang: §e" + requiredGroup.getDisplayName());

        return lore;
    }


    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("requiredGroup", requiredGroup.getName());

        return result;
    }

    public static GroupCondition deserialize(Map<String, Object> args) {
        String groupName = (String) args.getOrDefault("requiredGroup", "player");
        Group group = PermissionManager.getApi().getGroupManager().getGroup(groupName);

        return new GroupCondition(group);
    }
}
