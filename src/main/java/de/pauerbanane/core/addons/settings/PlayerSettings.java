package de.pauerbanane.core.addons.settings;

import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.settings.commands.SettingsCommand;

public class PlayerSettings extends Addon {

    @Override
    public void onEnable() {
        registerCommand(new SettingsCommand());
    }

    @Override
    public void onDisable() {

    }

}
