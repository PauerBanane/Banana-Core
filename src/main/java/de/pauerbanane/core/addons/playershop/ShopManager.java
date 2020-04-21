package de.pauerbanane.core.addons.playershop;

import com.google.common.collect.Maps;
import de.pauerbanane.core.BananaCore;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;

public class ShopManager {

    public static final NamespacedKey key = new NamespacedKey((Plugin) BananaCore.getInstance(), "playershop");

    private PlayerShop addon;

    private HashMap<UUID, ShopContent> content = Maps.newHashMap();

    private final HashMap<String, Shop> registeredShops;

    private final HashMap<UUID, BiConsumer<Player, Inventory>> contentEditor = Maps.newHashMap();

    private final File shopFolder;

    private static ShopManager instance;

    public ShopManager(PlayerShop addon) {
        this.instance = this;
        this.addon = addon;
        this.registeredShops = Maps.newHashMap();
        this.shopFolder = new File(addon.getShopFolder());
        if(!this.shopFolder.exists())
            this.shopFolder.mkdir();
        ShopContent.manager = this;

        load();
    }



    public boolean isShop(Villager villager) {
        return villager.getPersistentDataContainer().has(key, PersistentDataType.STRING);
    }

    public boolean isShopRegion(String regionID) {
        return this.registeredShops.containsKey(regionID);
    }

    public Shop getShopByRegion(String region) {
        return this.registeredShops.get(region);
    }

    public Shop getShop(String identifier) {
        return this.registeredShops.get(identifier);
    }

    public void load() {
        Collection<File> files = FileUtils.listFiles(shopFolder, new String[] {"yml"}, false);
        for (File shop : files) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(shop);
            Shop pshop = cfg.getSerializable("shop", Shop.class);
            this.registeredShops.put(pshop.getPlotRegion(), pshop);
            pshop.injectContentHolder();
            Location shopLocation = pshop.getLocation();
            shopLocation.getChunk().load();
            shopLocation.getChunk().setForceLoaded(true);
            Entity entity = shopLocation.getWorld().getEntity(pshop.getEntity());
            if(pshop.getOwner() != null) {
                pshop.setActive(true);
            }
            if (entity == null) {
                addon.getPlugin().getLogger().severe("Shop with ID " + pshop.getPlotRegion() + " has no valid linked entity, trying to respawn...");
                pshop.spawnEntity();
            }
            addon.getPlugin().getLogger().info("Loaded shop " + pshop.getPlotRegion());
        }
    }

    public Shop createShop(Location location, String plot) {
        Validate.isTrue(!this.registeredShops.containsKey(plot), "Shops must have an unique identifier.");
        Shop shop = new Shop(location.toCenterLocation().subtract(0.0D, 0.5D, 0.0D), plot);
        this.registeredShops.put(plot, shop);
        shop.spawnEntity();
        shop.setProfession(Villager.Profession.FISHERMAN);
        return shop;
    }

    public boolean deleteShop(String identifier) {
        if (!this.registeredShops.containsKey(identifier))
            return false;
        Shop shop = this.registeredShops.get(identifier);
        Entity entity = shop.getLocation().getWorld().getEntity(shop.getEntity());
        if (entity != null)
            entity.remove();
        File shopFile = new File(this.shopFolder, String.valueOf(shop.getPlotRegion()) + ".yml");
        if (shopFile.exists())
            shopFile.delete();
        this.registeredShops.remove(identifier);
        return true;
    }

    public void saveAllShops() {
        File datafolder = new File(addon.getShopFolder());
        this.registeredShops.values().forEach(shop -> {
            File shopFile = new File(addon.getShopFolder() +  String.valueOf(shop.getPlotRegion()) + ".yml");
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(shopFile);
            cfg.set("shop", shop);
            try {
                cfg.save(shopFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerEditor(Player player, ShopContent content) {
        this.content.put(player.getUniqueId(), content);
    }

    public void unregisterEditor(Player player, Inventory inventory) {
        ShopContent ct = this.content.get(player.getUniqueId());
        ItemStack[] items = inventory.getContents();
        ct.setStock(0);
        for(int i = 0; i < items.length; i++) {
            if(items[i] != null) {
                if (items[i].getType() == ct.getItem().getType()) {
                    ct.setStock(ct.getStock() + items[i].getAmount());
                } else
                    player.getInventory().addItem(items[i]);
            }
        }
    }

    public HashMap<String, Shop> getRegisteredShops() {
        return registeredShops;
    }

    public static ShopManager getInstance() {
        return instance;
    }

    public File getShopFolder() {
        return shopFolder;
    }
}