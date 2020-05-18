package de.pauerbanane.core.addons.schematicbrowser.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.core.addons.schematicbrowser.SchematicBrowser;
import de.pauerbanane.core.addons.schematicbrowser.gui.SchematicBrowserGUI;
import org.bukkit.entity.Player;

@CommandAlias("sb|schematicbrowser")
@CommandPermission("command.schematicbrowser")
public class SchematicBrowserCommand extends BaseCommand {

    private final SchematicBrowser addon;

    public SchematicBrowserCommand(SchematicBrowser addon) {
        this.addon = addon;
    }

    @Default
    public void open(Player sender) {
        SmartInventory.builder().title("Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(this.addon.getSchematicFolder())).build().open(sender);
    }

}
