package de.pauerbanane.core.addons.ranks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.ranks.commands.RankAdminCommands;
import de.pauerbanane.core.addons.ranks.commands.RankUserCommands;
import de.pauerbanane.core.data.conditions.Condition;
import de.pauerbanane.core.data.conditions.GroupCondition;
import de.pauerbanane.core.data.conditions.PlaytimeCondition;
import de.pauerbanane.core.data.conditions.VoteCondition;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.List;

public class Ranks extends Addon {

    private RankManager manager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Rank.class, "rank");

        this.manager = new RankManager(this);

        commandSetup();
    }

    private void commandSetup() {

        commandManager.getCommandCompletions().registerCompletion("rank", c -> {
           return ImmutableList.copyOf(manager.getRanks().keySet());
        });

        commandManager.getCommandContexts().registerContext(Rank.class, c -> {
            final String tag = c.popFirstArg();
            Rank rank = manager.getRank(tag);
            if (rank != null) {
                return rank;
            } else
                throw new InvalidCommandArgument("Dieser Rang existiert nicht.");
        });

        registerCommand(new RankAdminCommands(this));
        registerCommand(new RankUserCommands(this));
    }

    @Override
    public void onDisable() {
        manager.save();
    }

    @Override
    public void onReload() {

    }

    public RankManager getManager() {
        return manager;
    }
}
