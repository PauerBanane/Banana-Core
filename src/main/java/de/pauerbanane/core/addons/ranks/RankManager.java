package de.pauerbanane.core.addons.ranks;

import com.google.common.collect.Maps;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.model.user.User;
import org.apache.commons.compress.utils.Lists;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import javax.swing.text.rtf.RTFEditorKit;
import java.util.HashMap;

public class RankManager {

    private static RankManager instance;

    private HashMap<String, Rank> ranks;

    private Ranks addon;

    private FileLoader config;

    public RankManager(Ranks addon) {
        instance = this;
        this.addon = addon;
        this.ranks = Maps.newHashMap();

        load();
    }

    public boolean registerRank(Rank rank) {
        if (rank.getGroup() == null) return false;
        if (!ranks.containsKey(rank.getGroup().getName())) {
            ranks.put(rank.getGroup().getName(), rank);
            return true;
        } else
            return false;
    }

    public void removeRank(Rank rank) {
        this.getRanks().remove(rank.getGroup().getName());
    }

    private void load() {
        this.config = new FileLoader(addon.getAddonFolder(), "Ranks.yml");
        if (config.isSet("ranks")) {
            ConfigurationSection section = config.getConfigurationSection("ranks");
            for (String entry : section.getKeys(false)) {
                Rank rank = section.getSerializable(entry, Rank.class);
                if (rank == null) {
                    addon.getPlugin().getLogger().warning("Failed to load a Rank");
                    continue;
                }

                registerRank(rank);
            }
        }

        addon.getPlugin().getLogger().info("Loaded " + ranks.size() + " Ranks");
    }

    public void save() {
        config.set("ranks", null);

        int i = 0;
        for (Rank rank : ranks.values()) {
            config.set("ranks." + i, rank);
            i++;
        }

        config.save();
    }

    public Rank getRank(String name) {
        return ranks.get(name);
    }

    public static RankManager getInstance() {
        return instance;
    }

    public HashMap<String, Rank> getRanks() {
        return ranks;
    }
}
