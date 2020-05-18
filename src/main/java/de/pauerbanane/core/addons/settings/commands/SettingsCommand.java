package de.pauerbanane.core.addons.settings.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.core.addons.settings.SettingGUI;
import org.bukkit.entity.Player;

@CommandAlias("setting|settings|einstellungen")
public class SettingsCommand extends BaseCommand {

    @Default
    public void open(Player sender) {
        SmartInventory.builder().provider(new SettingGUI()).title("§eEinstellungen").size(4).build().open(sender);
    }

}
