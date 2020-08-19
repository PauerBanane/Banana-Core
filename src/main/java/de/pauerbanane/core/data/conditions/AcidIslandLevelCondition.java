package de.pauerbanane.core.data.conditions;


import com.google.common.collect.Lists;
import de.pauerbanane.core.util.UtilBentoBox;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("acidislandlevelcondition")
public class AcidIslandLevelCondition extends Condition {

    private int requiredLevel;

    public AcidIslandLevelCondition(int requiredLevel) {
        super.type = Type.ACIDISLAND_LEVEL_CONDITION;
        this.requiredLevel = requiredLevel;
    }

    @Override
    public boolean conditionAchieved(Player player) {
        return UtilBentoBox.getIslandLevel(player) >= requiredLevel;
    }

    @Override
    public List<String> requirementsAsLore(Player player) {
        List<String> lore = Lists.newArrayList();
        lore.add("§7Insel-Level: §e" + UtilBentoBox.getIslandLevel(player) + "§8/§e" + requiredLevel);

        return lore;
    }

    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("requiredLevel", requiredLevel);

        return result;
    }

    public static AcidIslandLevelCondition deserialize(Map<String, Object> args) {
        int required = (int) args.getOrDefault("requiredLevel", 999);

        return new AcidIslandLevelCondition(required);
    }
}
