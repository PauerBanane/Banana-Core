package de.pauerbanane.core.addons.ranks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.ranks.commands.RankAdminCommands;
import de.pauerbanane.core.addons.ranks.commands.RankUserCommands;
import de.pauerbanane.core.addons.ranks.conditions.GroupCondition;
import de.pauerbanane.core.addons.ranks.conditions.PlaytimeCondition;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import de.pauerbanane.core.addons.ranks.conditions.VoteCondition;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.List;

public class Ranks extends Addon {

    private RankManager manager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(Rank.class, "rank");
        ConfigurationSerialization.registerClass(GroupCondition.class, "groupCondition");
        ConfigurationSerialization.registerClass(VoteCondition.class, "voteCondition");
        ConfigurationSerialization.registerClass(PlaytimeCondition.class, "playtimeCondition");

        this.manager = new RankManager(this);

        commandSetup();
    }

    private void commandSetup() {
        commandManager.getCommandCompletions().registerCompletion("rankConditionType", c -> {
            List<String> conditionTypes = Lists.newArrayList();
            for(int i = 0; i < RankCondition.TYPE.values().length; i++)
                conditionTypes.add(RankCondition.TYPE.values()[i].toString().toLowerCase());
            return ImmutableList.copyOf(conditionTypes);
        });

        commandManager.getCommandContexts().registerContext(RankCondition.TYPE.class, c -> {
            final String tag = c.popFirstArg();
            RankCondition.TYPE type = null;
            for(int i = 0; i < RankCondition.TYPE.values().length; i++)
                if (RankCondition.TYPE.values()[i].toString().toLowerCase().equals(tag))
                    type = RankCondition.TYPE.values()[i];

            if (type != null) {
                return type;
            }   else
                throw new InvalidCommandArgument("Ungültige Voraussetzung eingegeben");
        });

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
