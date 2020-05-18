package de.pauerbanane.core.addons.settings.data;

import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Settings extends PlayerData {

    private static final NamespacedKey key = new NamespacedKey(BananaCore.getInstance(), "settings");

    private boolean scoreboard;

    private boolean phantoms;

    private boolean autoSit;

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration config) {
        config.set(key.getKey(), null);
        config.set(key.getKey() + ".scoreboard", scoreboard);
        config.set(key.getKey() + ".phantoms", phantoms);
        config.set(key.getKey() + ".autosit", autoSit);
    }

    @Override
    public void loadData(YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(key.getKey());
        if(section == null)
            return;

        scoreboard = section.getBoolean("scoreboard", true);
        phantoms = section.getBoolean("phantoms", true);
        autoSit = section.getBoolean("autosit", true);

        if(!scoreboard)
            BananaCore.getScoreboardAPI().getBoardManager().disableScoreboard(Bukkit.getPlayer(getOwner()));
    }

    public boolean scoreboardEnabled() {
        return scoreboard;
    }

    public void setScoreboardEnabled(boolean bool) {
        scoreboard = bool;
        if(!scoreboard) {
            BananaCore.getScoreboardAPI().getBoardManager().disableScoreboard(Bukkit.getPlayer(getOwner()));
        } else
            BananaCore.getScoreboardAPI().getBoardManager().enableScoreboard(Bukkit.getPlayer(getOwner()));
    }

    public boolean phantomSpawnEnabled() {
        return phantoms;
    }

    public void setPhantomSpawnEnabled(boolean bool) {
        phantoms = bool;
    }

    public boolean autoSitEnabled() {
        return autoSit;
    }

    public void setAutoSit(boolean bool) {
        autoSit = bool;
    }

}
