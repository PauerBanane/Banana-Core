package de.pauerbanane.core;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.PaperCommandManager;
import de.pauerbanane.api.data.PlayerDataManager;
import de.pauerbanane.core.addons.AddonManager;
import de.pauerbanane.core.addons.deathMessages.DeathMessages;
import de.pauerbanane.core.addons.essentials.Essentials;
import de.pauerbanane.core.commands.CommandSetup;
import de.pauerbanane.core.data.PermissionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class BananaCore extends JavaPlugin {

    private static BananaCore instance;

    private static String rootFolder,
                          addonFolder;

    private PaperCommandManager commandManager;

    private PlayerDataManager playerDataManager;

    private PermissionManager permissionManager;

    private PluginManager pluginManager;

    private AddonManager addonManager;

    private CommandSetup commandSetup;


    @Override
    public void onEnable() {
        this.instance = this;
        this.rootFolder = "plugins/Banana-Core/";
        this.addonFolder = rootFolder + "Addons/";
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getLocales().setDefaultLocale(Locale.GERMAN);

        this.playerDataManager = PlayerDataManager.getInstance();
        this.permissionManager = new PermissionManager(this);
        this.pluginManager = Bukkit.getPluginManager();
        this.addonManager = new AddonManager(this);
        this.commandSetup = new CommandSetup(this);

        commandSetup.registerCommandCompletions();
        commandSetup.registerCommandContexts();
        commandSetup.registerCommandConditions();

        registerAddons();
    }

    @Override
    public void onDisable() {
    }

    private void registerAddons() {
        addonManager.registerAddon(new DeathMessages(), "Todesnachrichten");
        addonManager.registerAddon(new Essentials(), "Essentials");
    }

    public void registerCommand(BaseCommand command) {
        commandManager.registerCommand(command);
    }
    public void unregisterCommand(BaseCommand command) {
        commandManager.unregisterCommand(command);
    }
    public void registerListener(Listener listener) {
        pluginManager.registerEvents(listener, this);
    }
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static BananaCore getInstance() {
        return instance;
    }

    public static String getRootFolder() {
        return rootFolder;
    }

    public static String getAddonFolder() {
        return addonFolder;
    }

    public PaperCommandManager getCommandManager() {
        return commandManager;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public AddonManager getAddonManager() {
        return addonManager;
    }
}
