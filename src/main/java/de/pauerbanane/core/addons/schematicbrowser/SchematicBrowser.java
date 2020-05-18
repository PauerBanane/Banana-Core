package de.pauerbanane.core.addons.schematicbrowser;

import com.sk89q.worldedit.WorldEdit;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.schematicbrowser.commands.SchematicBrowserCommand;

import java.io.File;

public class SchematicBrowser extends Addon {

    private static SchematicBrowser instance;

    private File schematicFolder;

    @Override
    public void onEnable() {
        this.instance = this;
        File saveDir = new File((WorldEdit.getInstance().getConfiguration()).saveDir);
        if (saveDir.exists()) {
            this.schematicFolder = saveDir;
            plugin.getLogger().info("Using WorldEdit's schematic directory with path: " + saveDir.getAbsolutePath());
        } else {
            File fallback = new File(plugin.getDataFolder() + File.separator + "schematics");
            if (!fallback.exists())
                fallback.mkdirs();
            this.schematicFolder = fallback;
            plugin.getLogger().info("Using fallback schematic directory.");
        }

        registerCommand(new SchematicBrowserCommand(this));
    }

    @Override
    public void onDisable() {

    }

    public File getSchematicFolder() {
        return schematicFolder;
    }

    public static SchematicBrowser getInstance() {
        return instance;
    }
}
