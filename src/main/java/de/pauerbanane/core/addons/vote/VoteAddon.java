package de.pauerbanane.core.addons.vote;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.UtilItem;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import org.bukkit.inventory.ItemStack;

public class VoteAddon extends Addon {

    private VoteChestManager voteChestManager;

    private VoteManager voteManager;

    @Override
    public void onEnable() {
        this.voteChestManager = new VoteChestManager(this);
        this.voteManager = new VoteManager(this);
    }

    @Override
    public void onDisable() {
        voteChestManager.save();
    }

    @Override
    public void onReload() {

    }
}
