package de.pauerbanane.core.addons;

import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.PaperCommandManager;
import de.pauerbanane.api.npcattachment.NPCInteractAttachment;
import de.pauerbanane.core.BananaCore;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.io.File;
import java.util.ArrayList;

public abstract class Addon {

    protected String name;

    protected Boolean enabled = false;

    protected BananaCore plugin = BananaCore.getInstance();

    protected PaperCommandManager commandManager = plugin.getCommandManager();

    protected PluginManager pluginManager = plugin.getPluginManager();

    protected ArrayList<BaseCommand> registeredCommands = Lists.newArrayList();

    protected ArrayList<Listener> registeredListeners = Lists.newArrayList();

    public void enable() {
        enabled = true;
        onEnable();
    }

    public void disable() {
        registeredCommands.iterator().forEachRemaining(command -> plugin.unregisterCommand(command));
        registeredCommands.clear();
        registeredListeners.forEach(listener -> plugin.unregisterListener(listener));
        registeredListeners.clear();
        onDisable();
        enabled = false;
    }

    public void registerNPCAttachment(NPCInteractAttachment attachment) {
        BananaCore.getApi().getNpcDataHandler().registerAttachment(attachment);
    }

    public void registerCommand(BaseCommand command) {
        plugin.registerCommand(command);
        registeredCommands.add(command);
    }
    public void unregisterCommand(BaseCommand command) {
        plugin.unregisterCommand(command);
        registeredCommands.remove(command);
    }
    public void registerListener(Listener listener) {
        plugin.registerListener(listener);
        registeredListeners.add(listener);
    }
    public void unregisterListener(Listener listener) {
        plugin.unregisterListener(listener);
        registeredListeners.remove(listener);
    }

    public abstract void onEnable();
    public abstract void onDisable();

    public String getName() {
        return name;
    }

    public String getAddonFolder() {
        return plugin.getDataFolder().getPath() + File.separator + "Addons" + File.separator;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public BananaCore getPlugin() {
        return plugin;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }
}
