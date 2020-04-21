package de.pauerbanane.core.addons.playershop;

import com.google.common.collect.ImmutableList;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.playershop.listener.ShopListener;
import de.pauerbanane.core.addons.playershop.oldContent.PlayerShopAttachment;
import de.pauerbanane.core.addons.playershop.oldContent.ShopStorageManager;
import de.pauerbanane.core.addons.plotshop.PlotShop;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;

public class PlayerShop extends Addon {

    private static PlayerShop instance;

    private ShopManager manager;

    private ShopStorageManager storageManager;

    @Override
    public void onEnable() {
        if(PlotShop.getInstance() == null) {
            plugin.getLogger().warning("Failed to load PlayerShop-Addon - PlotShop-Addon is not loaded");
            return;
        }
        this.instance = this;
        ConfigurationSerialization.registerClass(ShopContent.class);
        ConfigurationSerialization.registerClass(Shop.class);
        this.manager = new ShopManager(this);
        this.storageManager = new ShopStorageManager(this);
        ShopContent.setManager(manager);

        commandSetup();

        registerCommand(new ShopCommand(manager));

        registerListener(new ShopListener(manager, storageManager));

        registerNPCAttachment(new PlayerShopAttachment());
    }

    @Override
    public void onDisable() {
        manager.saveAllShops();
    }

    private void commandSetup() {
        commandManager.getCommandContexts().registerContext(Shop.class, c -> {
            final String tag = c.popFirstArg();
            Shop shop = manager.getShop(tag);
            if(shop != null) {
                return shop;
            } else
                throw new InvalidCommandArgument("Invalid Shop specified.");
        });

        commandManager.getCommandCompletions().registerCompletion("shop", c -> ImmutableList.copyOf(manager.getRegisteredShops().keySet()));
    }

    public String getShopFolder() {
        String path = plugin.getDataFolder().getPath() + File.separator + "shops" + File.separator;
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();

        return path;
    }

    public String getDataFolder() {
        String path = plugin.getDataFolder().getPath() + File.separator + "shops" + File.separator + "savedData" + File.separator;
        File file = new File(path);
        if(!file.exists())
            file.mkdirs();

        return path;
    }

    public ShopStorageManager getStorageManager() {
        return storageManager;
    }

    public static PlayerShop getInstance() {
        return instance;
    }

    public ShopManager getManager() {
        return manager;
    }

}
