package de.pauerbanane.core.addons.ranks.conditions;

import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.data.PermissionManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class RankCondition implements ConfigurationSerializable {

    protected TYPE type;

    public abstract boolean conditionAchieved(Player player);
    public abstract List<String> requirementsAsLore(Player player);


    public enum TYPE {
        GROUP_CONDITION,
        VOTE_CONDITION,
        PLAYTIME_CONDITION;
    }

    public static boolean isValidValue(TYPE type, String value) {
        if (type == TYPE.GROUP_CONDITION) {
            return PermissionManager.getApi().getGroupManager().getGroup(value) != null;
        } else if (type == TYPE.VOTE_CONDITION || type == TYPE.PLAYTIME_CONDITION) {
            return UtilMath.isInt(value) && Integer.parseInt(value) > 0;
        } else
            return false;
    }

    public TYPE getType() {
        return type;
    }

}
