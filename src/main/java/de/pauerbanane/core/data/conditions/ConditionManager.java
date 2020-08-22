package de.pauerbanane.core.data.conditions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.data.conditions.mcmmo.FishingCondition;
import de.pauerbanane.core.data.conditions.mcmmo.HerbalismCondition;
import de.pauerbanane.core.data.conditions.mcmmo.MiningCondition;
import de.pauerbanane.core.data.conditions.mcmmo.WoodcuttingCondition;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.List;

public class ConditionManager {

    private BananaCore plugin;

    public ConditionManager(BananaCore plugin) {
        this.plugin = plugin;

        ConfigurationSerialization.registerClass(GroupCondition.class, "groupCondition");
        ConfigurationSerialization.registerClass(VoteCondition.class, "voteCondition");
        ConfigurationSerialization.registerClass(PlaytimeCondition.class, "playtimeCondition");
        ConfigurationSerialization.registerClass(AcidIslandLevelCondition.class, "acidislandlevelcondition");
        
        ConfigurationSerialization.registerClass(MiningCondition.class, "miningCondition");
        ConfigurationSerialization.registerClass(WoodcuttingCondition.class, "woodcuttingCondition");
        ConfigurationSerialization.registerClass(HerbalismCondition.class, "herbalismCondition");
        ConfigurationSerialization.registerClass(FishingCondition.class, "fishingCondition");

        commandSetup();
    }

    private void commandSetup() {
        plugin.getCommandManager().getCommandCompletions().registerCompletion("condition", c -> {
            List<String> conditionTypes = Lists.newArrayList();
            for(int i = 0; i < Condition.Type.values().length; i++)
                conditionTypes.add(Condition.Type.values()[i].toString().toLowerCase());
            return ImmutableList.copyOf(conditionTypes);
        });

        plugin.getCommandManager().getCommandContexts().registerContext(Condition.Type.class, c -> {
            final String tag = c.popFirstArg();
            Condition.Type type = null;
            for(int i = 0; i < Condition.Type.values().length; i++)
                if (Condition.Type.values()[i].toString().toLowerCase().equals(tag))
                    type = Condition.Type.values()[i];

            if (type != null) {
                return type;
            }   else
                throw new InvalidCommandArgument("Ungültige Voraussetzung eingegeben.");
        });
    }



}
