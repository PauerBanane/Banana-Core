package de.pauerbanane.core;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.PaperCommandManager;
import de.pauerbanane.api.BananaAPI;
import de.pauerbanane.api.addons.AddonManager;
import de.pauerbanane.api.data.PlayerDataManager;
import de.pauerbanane.api.scoreboards.ScoreboardAPI;
import de.pauerbanane.core.addons.afk.AFK;
import de.pauerbanane.core.addons.beds.Beds;
import de.pauerbanane.core.addons.carriages.Carriages;
import de.pauerbanane.core.addons.chairs.Chairs;
import de.pauerbanane.core.addons.chat.PluginMessageChat;
import de.pauerbanane.core.addons.chestlock.ChestLock;
import de.pauerbanane.core.addons.deathMessages.DeathMessages;
import de.pauerbanane.core.addons.essentials.Essentials;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.addons.infos.Infos;
import de.pauerbanane.core.addons.lobby.Lobby;
import de.pauerbanane.core.addons.permissionshop.PermissionShop;
import de.pauerbanane.core.addons.phantomhandler.PhantomHandler;
import de.pauerbanane.core.addons.playershop.PlayerShop;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import de.pauerbanane.core.addons.portals.Portals;
import de.pauerbanane.core.addons.resourcepack.Resourcepack;
import de.pauerbanane.core.addons.schematicbrowser.SchematicBrowser;
import de.pauerbanane.core.addons.settings.PlayerSettings;
import de.pauerbanane.core.addons.settings.data.Settings;
import de.pauerbanane.core.addons.snowcontrol.SnowControl;
import de.pauerbanane.core.addons.ultrahardcore.UltraHardcore;
import de.pauerbanane.core.addons.vote.VoteAddon;
import de.pauerbanane.core.addons.votifier.Votifier;
import de.pauerbanane.core.commands.AddonCommand;
import de.pauerbanane.core.commands.CheckPermissionCommand;
import de.pauerbanane.core.commands.CommandSetup;
import de.pauerbanane.core.commands.CustomItemCommand;
import de.pauerbanane.core.data.PermissionManager;
import de.pauerbanane.core.data.PlayerDataLoader;
import de.pauerbanane.core.listener.DisableJoinQuitMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;

import java.util.Locale;

public class BananaCore extends JavaPlugin {

    private static BananaCore instance;

    private static BananaAPI api;

    private static Economy economy;

    private static String rootFolder,
                          addonFolder;

    private static DynmapCommonAPI dynmapAPI;

    private static ScoreboardAPI scoreboardAPI;

    private PaperCommandManager commandManager;

    private PlayerDataManager playerDataManager;

    private PermissionManager permissionManager;

    private PluginManager pluginManager;

    private AddonManager addonManager;

    private CommandSetup commandSetup;

    private PlayerDataLoader playerDataLoader;

    private String serverName;



    @Override
    public void onEnable() {
        this.instance = this;
        this.api = BananaAPI.getInstance();
        this.scoreboardAPI = new ScoreboardAPI(this);
        this.rootFolder = "plugins/Banana-Core/";
        this.addonFolder = rootFolder + "Addons/";
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getLocales().setDefaultLocale(Locale.GERMAN);
        this.initconfig();

        this.playerDataManager = PlayerDataManager.getInstance();
        this.permissionManager = new PermissionManager(this);
        this.pluginManager = Bukkit.getPluginManager();
        this.addonManager = new AddonManager(this, commandManager);
        this.commandSetup = new CommandSetup(this);
        this.playerDataLoader = new PlayerDataLoader(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        playerDataManager.registerPlayerData(this, HomeData.class);
        playerDataManager.registerPlayerData(this, Settings.class);

        commandSetup.registerCommandCompletions();
        commandSetup.registerCommandContexts();

        if(!setupEconomy() && !serverName.equalsIgnoreCase("lobby") && !serverName.equalsIgnoreCase("ultra-hardcore")) {
            getLogger().severe("Es konnte kein Economy Plugin gefunden werden!");
            getLogger().severe("Plugin wird nicht geladen.");
            return;
        }

        setupDynmap();

        playerDataLoader.loadOnlinePlayers();

        registerAddons();

        registerCommand(new AddonCommand());
        registerCommand(new CheckPermissionCommand());
        registerCommand(new CustomItemCommand());

        registerListener(new DisableJoinQuitMessage());

    }

    @Override
    public void onDisable() {
        addonManager.shutdown();
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer("§7Der Server startet neu"));
    }

    private boolean setupEconomy() {
        if (this.getServer().getPluginManager().getPlugin("Vault") == null)
            return false;

        RegisteredServiceProvider<Economy> rsp = this.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
            return false;

        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupDynmap() {
        dynmapAPI = (DynmapCommonAPI) Bukkit.getServer().getPluginManager().getPlugin("Dynmap");

        getLogger().info("Trying to hook into Dynmap...");
        if(dynmapAPI == null) {
            getLogger().warning("Failed to hook into Dynmap - Dynmap Support is not activated");
        } else {
            getLogger().info("Successfully hooked into Dynmap");
        }

        return dynmapAPI != null;
    }

    private void initconfig() {
        if(!getConfig().isSet("server")) {
            getConfig().set("server", "survival");
            saveConfig();
        }
        this.serverName = getConfig().getString("server");
    }

    private void registerAddons() {
        addonManager.registerAddon(new AFK(), "AFK");
        addonManager.registerAddon(new Carriages(), "Carriages");
        addonManager.registerAddon(new Chairs(), "Chairs");
        addonManager.registerAddon(new PluginMessageChat(), "Chat");
        addonManager.registerAddon(new ChestLock(), "ChestLock");
        addonManager.registerAddon(new PlayerSettings(), "Einstellungen");
        addonManager.registerAddon(new Essentials(), "Essentials");
        addonManager.registerAddon(new Beds(), "ImprovedBeds");
        addonManager.registerAddon(new Infos(), "Infos");
        addonManager.registerAddon(new Lobby(), "Lobby");
        addonManager.registerAddon(new PermissionShop(), "PermissionShop");
        addonManager.registerAddon(new PhantomHandler(), "Phantom-Manager");
        addonManager.registerAddon(new PlotShop(), "Plots");
        addonManager.registerAddon(new Portals(), "Portal");
        addonManager.registerAddon(new Resourcepack(), "Resourcepack");
        addonManager.registerAddon(new SchematicBrowser(), "SchematicBrowser");
        addonManager.registerAddon(new PlayerShop(), "Shops");
        addonManager.registerAddon(new SnowControl(), "SnowControl");
        addonManager.registerAddon(new DeathMessages(), "Todesnachrichten");
        addonManager.registerAddon(new UltraHardcore(), "Ultra-Hardcore");
        addonManager.registerAddon(new VoteAddon(), "VoteAddon");
        addonManager.registerAddon(new Votifier(), "Votifier");
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

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public static Economy getEconomy() {
        return economy;
    }

    public static BananaAPI getApi() {
        return api;
    }

    public String getServerName() {
        return this.serverName;
    }

    public static ScoreboardAPI getScoreboardAPI() {
        return scoreboardAPI;
    }

    public static DynmapCommonAPI getDynmapAPI() {
        return dynmapAPI;
    }

    public boolean hasDynmapSupport() {
        return dynmapAPI != null;
    }
}
