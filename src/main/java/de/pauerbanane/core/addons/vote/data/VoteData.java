package de.pauerbanane.core.addons.vote.data;

import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class VoteData extends PlayerData {

    private static NamespacedKey key = new NamespacedKey(BananaCore.getInstance(), "voteData");

    private int oldKeys = 0,
                ancientKeys = 0,
                epicKeys = 0;

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration config) {
        config.set(key.getKey(), null);
        config.set(key.getKey() + ".oldKey", oldKeys);
        config.set(key.getKey() + ".ancientKey", ancientKeys);
        config.set(key.getKey() + ".epicKey", epicKeys);
    }

    @Override
    public void loadData(YamlConfiguration config) {
        ConfigurationSection section = config.getConfigurationSection(key.getKey());
        if(section == null) return;

        oldKeys = section.getInt("oldKey");
        ancientKeys = section.getInt("ancientKey");
        epicKeys = section.getInt("epicKey");
    }

    public int getVoteKeys(VoteKey.Type voteKey) {
        if (voteKey == VoteKey.Type.OLD_KEY) return oldKeys;
        if (voteKey == VoteKey.Type.ANCIENT_KEY) return ancientKeys;
        if (voteKey == VoteKey.Type.EPIC_KEY) return epicKeys;
        return 0;
    }

    public void setVoteKeys(VoteKey.Type type, int amount) {
        if (type == VoteKey.Type.OLD_KEY) oldKeys = amount;
        if (type == VoteKey.Type.ANCIENT_KEY) ancientKeys = amount;
        if (type == VoteKey.Type.EPIC_KEY) epicKeys = amount;
    }
}
