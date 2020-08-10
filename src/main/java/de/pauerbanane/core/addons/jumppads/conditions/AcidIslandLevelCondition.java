package de.pauerbanane.core.addons.jumppads.conditions;


import com.google.common.collect.Lists;
import de.pauerbanane.core.addons.ranks.conditions.VoteCondition;
import de.pauerbanane.core.sql.DatabaseManager;
import de.pauerbanane.core.util.UtilBentoBox;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import world.bentobox.bentobox.BentoBox;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("acidislandlevelcondition")
public class AcidIslandLevelCondition extends JumppadCondition {

    private int requiredLevel;

    public AcidIslandLevelCondition(int requiredLevel) {
        super.type = Type.AcidIsland_Level_Condition;
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
