package de.pauerbanane.core.util;

import de.pauerbanane.api.util.UtilMath;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class UtilMcMMO {

    public enum SkillType {
        BERGBAU, HOLZFÄLLEN, ANGELN, KRÄUTERKUNDE;

        public static SkillType getSkillType(String value) {
            for (int i = 0; i < SkillType.values().length; i++) {
                SkillType type = SkillType.values()[i];
                if (type.toString().equalsIgnoreCase(value))
                    return type;
            }
            return null;
        }
    }

    public static int getSkillLevel(Player player, SkillType type) {
        String parsed = PlaceholderAPI.setPlaceholders(player, "%mcmmo_level_" + type.toString().toLowerCase() + "%");
        return UtilMath.isInt(parsed) ? Integer.parseInt(parsed) : 0;
    }

}
