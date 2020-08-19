package de.pauerbanane.core.addons.vote.data;

import com.google.common.collect.Maps;
import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class VoteData extends PlayerData {

    private static NamespacedKey key = new NamespacedKey(BananaCore.getInstance(), "voteData");

    private HashMap<VoteKey, Integer> voteKeys = Maps.newHashMap();

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration config) {
        config.set(key.getKey(), null);

        voteKeys.keySet().forEach(voteKey -> {
            config.set(key.getKey() + "." + voteKey.getName(), voteKeys.get(voteKey));
        });
    }

    @Override
    public void loadData(YamlConfiguration config) {
        VoteChestManager manager = VoteChestManager.getInstance();
        if (manager == null) return;

        ConfigurationSection section = config.getConfigurationSection(key.getKey());
        if(section == null) return;

        for (String key : section.getKeys(false)) {
            VoteKey voteKey = manager.getVoteKey(key);
            if (voteKey != null)
                addVoteKey(voteKey, section.getInt(key));
        }
    }

    public int getVoteKeys(VoteKey voteKey) {
        if (!voteKeys.containsKey(voteKey)) return 0;
        return voteKeys.get(voteKey);
    }

    public void addVoteKey(VoteKey voteKey, int amount) {
        if (voteKeys.containsKey(voteKey)) {
            voteKeys.put(voteKey, voteKeys.get(voteKey) + amount);
        } else
            voteKeys.put(voteKey, amount);
    }

    public void removeVoteKey(VoteKey voteKey, int amount) {
        if (!voteKeys.containsKey(voteKey) || voteKeys.get(voteKey) < amount) return;
        voteKeys.put(voteKey, voteKeys.get(voteKey) - amount);
    }

    public void resetVoteKey(VoteKey voteKey) {
        voteKeys.remove(voteKey);
    }

    public void setVoteKey(VoteKey voteKey, int value) {
        voteKeys.put(voteKey, value);
    }

}
