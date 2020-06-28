package de.pauerbanane.core.addons.settings;

import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.core.addons.settings.commands.SettingsCommand;

public class PlayerSettings extends Addon {

    @Override
    public void onEnable() {
        registerCommand(new SettingsCommand());
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

}
