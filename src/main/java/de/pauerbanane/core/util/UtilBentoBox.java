package de.pauerbanane.core.util;

import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class UtilBentoBox {

    private static final String ISLAND_LEVEL = "%Level_acidisland_island_level%";

    public static boolean hasBentoboxSupport() {
        return BananaCore.getInstance().getPluginManager().getPlugin("BentoBox") != null;
    }

    public static int getIslandLevel(Player player) {
        if (!hasBentoboxSupport()) return 0;
        String level = PlaceholderAPI.setPlaceholders(player, ISLAND_LEVEL);
        return UtilMath.isInt(level) ? Integer.parseInt(level) : 0;
    }

}
