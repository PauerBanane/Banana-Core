package de.pauerbanane.core.addons.jumppads;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.jumppads.commands.JumppadCommand;
import de.pauerbanane.core.addons.jumppads.conditions.AcidIslandLevelCondition;
import de.pauerbanane.core.addons.jumppads.conditions.JumppadCondition;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;
import java.util.List;

public class JumppadAddon extends Addon {

    private JumppadManager manager;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(AcidIslandLevelCondition.class, "acidislandlevelcondition");
        ConfigurationSerialization.registerClass(JumppadVector.class, "jumppadvector");
        ConfigurationSerialization.registerClass(Jumppad.class, "jumppad");
        this.manager = new JumppadManager(this);

        commandSetup();
    }

    private void commandSetup() {
        commandManager.getCommandCompletions().registerCompletion("jumppad", c -> {
            return ImmutableList.copyOf(manager.getWorldJumppadRegions(c.getPlayer().getWorld()));
        });
        commandManager.getCommandContexts().registerContext(Jumppad.class, c -> {
            final String tag = c.popFirstArg();
            Jumppad jumppad = manager.getJumppad(c.getPlayer().getWorld(), tag);
            if (jumppad != null) {
                return jumppad;
            } else
                throw new InvalidCommandArgument("Dieses Jumppad existiert nicht in dieser Welt.");
        });

        commandManager.getCommandCompletions().registerCompletion("jumppadcondition", c -> {
            List<String> conditionTypes = Lists.newArrayList();
            for(int i = 0; i < JumppadCondition.Type.values().length; i++)
                conditionTypes.add(JumppadCondition.Type.values()[i].toString().toLowerCase());
            return ImmutableList.copyOf(conditionTypes);
        });

        commandManager.getCommandContexts().registerContext(JumppadCondition.Type.class, c -> {
            final String tag = c.popFirstArg();
            JumppadCondition.Type type = null;
            for(int i = 0; i < JumppadCondition.Type.values().length; i++)
                if (JumppadCondition.Type.values()[i].toString().toLowerCase().equals(tag))
                    type = JumppadCondition.Type.values()[i];

            if (type != null) {
                return type;
            }   else
                throw new InvalidCommandArgument("Ungültige Voraussetzung eingegeben.");
        });

        registerCommand(new JumppadCommand(manager));
    }

    @Override
    public void onDisable() {
        manager.save();
    }

    @Override
    public void onReload() {

    }
}
