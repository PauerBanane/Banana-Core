package de.pauerbanane.core.addons.vote;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.votifier.Vote;

public class VoteAddon extends Addon {

    private VoteChestManager manager;

    @Override
    public void onEnable() {
        this.manager = new VoteChestManager(this, commandManager);
        new VoteReceiver(manager);
        BananaCore.getInstance().getPlayerDataManager().registerPlayerData(plugin, VoteData.class);
    }

    @Override
    public void onDisable() {
        manager.save();
    }

    @Override
    public void onReload() {

    }
}
