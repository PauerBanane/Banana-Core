package de.pauerbanane.core.addons.jumppads.conditions;

import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import de.pauerbanane.core.data.PermissionManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import javax.xml.bind.Marshaller;
import java.util.List;

public abstract class JumppadCondition implements ConfigurationSerializable {

    protected JumppadCondition.Type type;

    public abstract boolean conditionAchieved(Player player);
    public abstract List<String> requirementsAsLore(Player player);

    public enum Type {
        AcidIsland_Level_Condition;
    }

    public static boolean isValidValue(JumppadCondition.Type type, String value) {
        if (type == Type.AcidIsland_Level_Condition) {
            return UtilMath.isInt(value);
        } else
            return false;
    }

    public Type getType() {
        return type;
    }
}
