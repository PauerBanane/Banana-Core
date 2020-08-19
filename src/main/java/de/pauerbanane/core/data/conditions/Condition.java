package de.pauerbanane.core.data.conditions;

import com.comphenix.protocol.PacketType;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.data.PermissionManager;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class Condition implements ConfigurationSerializable {

    protected Type type;

    public abstract boolean conditionAchieved(Player player);
    public abstract List<String> requirementsAsLore(Player player);


    public enum Type {
        GROUP_CONDITION,
        VOTE_CONDITION,
        PLAYTIME_CONDITION,
        ACIDISLAND_LEVEL_CONDITION;
    }

    public static boolean isValidValue(Type type, String value) {
        if (type == Type.GROUP_CONDITION) {
            return PermissionManager.getApi().getGroupManager().getGroup(value) != null;
        } else if  (type == Type.VOTE_CONDITION ||
                    type == Type.PLAYTIME_CONDITION ||
                    type == Type.ACIDISLAND_LEVEL_CONDITION) {
            return UtilMath.isInt(value) && Integer.parseInt(value) > 0;
        } else
            return false;
    }

    public Type getType() {
        return type;
    }

    public static class Builder {

        private Type type;

        private String value;

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }

        public Condition build() {
            if (type == null || value == null || !isValidValue(type, value)) return null;
            switch (type) {
                case VOTE_CONDITION:
                    return new VoteCondition(Integer.parseInt(value));
                case GROUP_CONDITION:
                    return new GroupCondition(PermissionManager.getApi().getGroupManager().getGroup(value));
                case PLAYTIME_CONDITION:
                    return new PlaytimeCondition(Integer.parseInt(value));
                case ACIDISLAND_LEVEL_CONDITION:
                    return new AcidIslandLevelCondition(Integer.parseInt(value));
            }

            return null;
        }
    }

}
