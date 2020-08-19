package de.pauerbanane.core.addons.jumppads;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.jumppads.commands.JumppadCommand;
import de.pauerbanane.core.data.conditions.AcidIslandLevelCondition;
import de.pauerbanane.core.data.conditions.Condition;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.List;

public class JumppadAddon extends Addon {

    private JumppadManager manager;

    @Override
    public void onEnable() {
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
