package de.pauerbanane.core.data.conditions.mcmmo;

import com.google.common.collect.Lists;
import de.pauerbanane.core.data.conditions.Condition;
import de.pauerbanane.core.sql.DatabaseManager;
import de.pauerbanane.core.util.UtilMcMMO;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("woodcuttingCondition")
public class WoodcuttingCondition extends Condition {

    private int requiredLevel;

    public WoodcuttingCondition(int requiredLevel) {
        super.type = Type.MCMMO_WOODUTTING_LEVEL_CONDITION;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public boolean conditionAchieved(Player player) {
        return UtilMcMMO.getSkillLevel(player, UtilMcMMO.SkillType.HOLZFÄLLEN) >= requiredLevel;
    }

    @Override
    public List<String> requirementsAsLore(Player player) {
        List<String> lore = Lists.newArrayList();
        lore.add("§7Holfzällen Level: §e" + UtilMcMMO.getSkillLevel(player, UtilMcMMO.SkillType.HOLZFÄLLEN) + "§8/§e" + requiredLevel);

        return lore;
    }


    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("requiredLevel", requiredLevel);

        return result;
    }

    public static WoodcuttingCondition deserialize(Map<String, Object> args) {
        int amount = (int) args.getOrDefault("requiredLevel", 999);

        return new WoodcuttingCondition(amount);
    }
}
