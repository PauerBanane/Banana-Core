package de.pauerbanane.core.addons.playershop.oldContent;

import com.google.common.collect.Maps;
import de.pauerbanane.api.util.UtilInv;
import de.pauerbanane.core.addons.playershop.PlayerShop;
import de.pauerbanane.core.addons.playershop.Shop;
import de.pauerbanane.core.addons.playershop.ShopContent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ShopStorageManager {

    private PlayerShop addon;

    private final File dataFolder;

    private HashMap<UUID, ItemStack> storageEditor;

    public ShopStorageManager(PlayerShop addon) {
        this.addon = addon;
        this.storageEditor = Maps.newHashMap();
        this.dataFolder = new File(addon.getDataFolder());
        if(!this.dataFolder.exists())
            this.dataFolder.mkdir();
    }

    public void saveOnReset(Shop shop) {
        if(shop.getOwner() == null) return;

        File file = new File(dataFolder, shop.getOwner().toString() + ".yml");
        if(file.exists()) {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        int i = 0;
        for(ShopContent content : shop.getShopContent()) {
            if(content.getStock() > 0) {
                ItemStack item = content.getItem();
                item.setAmount(content.getStock());
                config.set("content." + i, item);
                i++;
            }
        }

        addon.getPlugin().getLogger().info("Saved " + i + " contents at Shop-Reset from id " + shop.getPlotRegion());

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!config.isSet("content"))
            file.delete();
    }

    public boolean hasOldContents(Player player) {
        File file = new File(dataFolder, player.getUniqueId().toString() + ".yml");
        if(!file.exists())
            return false;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        if(!config.isSet("content")) {
            file.delete();
            return false;
        }
        return config.getConfigurationSection("content").getKeys(false).size() > 0;
    }

    public ArrayList<ItemStack> receiveStoragedItems(Player player) {
        if(!hasOldContents(player)) return null;

        ArrayList<ItemStack> items = new ArrayList<>();

        File file = new File(dataFolder, player.getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection section = config.getConfigurationSection("content");
        for (String number : section.getKeys(false)) {
            ItemStack item = section.getItemStack(number);
            items.add(item);
        }

        return items;
    }

    public void registerStoragedEditor(Player player, ItemStack item) {
        this.storageEditor.put(player.getUniqueId(), item);

        Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 54, "Alter Warenbestand");
        UtilInv.insert(inventory, item, false, null);
        player.openInventory(inventory);
    }

    public void unregisterStoragedEditor(Player player, Inventory inventory) {
        ArrayList<ItemStack> items = receiveStoragedItems(player);
        ItemStack storagedItem = null;
        for(ItemStack itemStack : items)
            if (itemStack.clone().asOne().isSimilar(storageEditor.get(player.getUniqueId()).clone().asOne()))
                storagedItem = itemStack;

        if(storagedItem == null) return;

        ItemStack[] invItems = inventory.getContents();

        int amount = 0;
        for(int i = 0; i < invItems.length; i++) {
            if(invItems[i] != null) {
                if (invItems[i].clone().asOne().isSimilar(storagedItem.clone().asOne())) {
                    amount += invItems[i].getAmount();
                } else
                    player.getInventory().addItem(invItems[i]);
            }
        }

        storagedItem.setAmount(amount);
        storageItems(player, items);
    }

    public void storageItems(Player player, ArrayList<ItemStack> items) {
        File file = new File(dataFolder, player.getUniqueId().toString() + ".yml");
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        config.set("content", null);
        int i = 0;
        for(ItemStack item : items) {
            if(item.getAmount() > 0) {
                config.set("content." + i, item);
                i++;
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!config.isSet("content"))
            file.delete();
    }



}
