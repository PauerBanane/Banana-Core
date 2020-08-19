package de.pauerbanane.core.addons.quests;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.addons.AddonManager;
import me.blackvein.quests.Quests;

public class QuestsAddon extends Addon {

    private Quests quests;

    @Override
    public void onEnable() {
        if (!hookIntoQuests()) {
            plugin.getLogger().warning("Failed to load Quests-Addon - Quests not found");
            AddonManager.getInstance().disableAddon(this);
            return;
        }


    }

    private boolean hookIntoQuests() {
        this.quests = (Quests) pluginManager.getPlugin("Quests");
        return quests != null;
    }


    @Override
    public void onDisable() {
        if (quests == null) return;
    }

    @Override
    public void onReload() {

    }
}
