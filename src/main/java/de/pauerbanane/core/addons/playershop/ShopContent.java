package de.pauerbanane.core.addons.playershop;

import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilInv;
import de.pauerbanane.core.BananaCore;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ShopContent implements ConfigurationSerializable {

    private static Economy eco = BananaCore.getEconomy();

    protected static ShopManager manager;

    private Shop shop;

    private ItemStack item;

    private int stock;

    private double sellPrice;

    private double purchasePrice;

    private boolean sellEnabled = false;

    private boolean purchaseEnabled = false;

    public ShopContent(ItemStack item) {
        Validate.notNull(item, "Item cannot be null");
        this.item = item.asOne();
        this.stock = 0;
        this.sellPrice = 1.0D;
        this.purchasePrice = 1.0D;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("item", item);
        result.put("stock", stock);
        result.put("sellPrice", sellPrice);
        result.put("purchasePrice", purchasePrice);
        result.put("sellEnabled", sellEnabled);
        result.put("purchaseEnabled", purchaseEnabled);

        return result;
    }
    public static ShopContent deserialize(Map<String, Object> args) {
        ItemStack item = (ItemStack) args.get("item");
        ShopContent content = new ShopContent(item);
        content.setStock(Integer.valueOf((Integer) args.get("stock")));
        content.setSellPrice(Double.valueOf((Double) args.get("sellPrice")));
        content.setPurchasePrice(Double.valueOf((Double) args.get("purchasePrice")));
        content.setSellEnabled((Boolean) args.get("sellEnabled"));
        content.setPurchaseEnabled((Boolean) args.get("purchaseEnabled"));

        return content;
    }

    public boolean purchase(Player player, int amount) {
        if (!this.sellEnabled) {
            player.sendMessage(F.error("Shop", "Dieses Item ist nicht kaufbar."));
            return false;
        }
        if (this.stock - amount < 0) {
            player.sendMessage(F.error("Shop", "Dieser Shop hat nicht genug Ware für diese Transaktion."));
            return false;
        }
        if (eco.withdrawPlayer((OfflinePlayer)player, this.sellPrice * amount).transactionSuccess()) {
            this.stock -= amount;
            player.getInventory().addItem(new ItemStack[] { this.item.clone().asQuantity(amount) });
            this.shop.depositBalance(this.sellPrice * amount);
            return true;
        }
        player.sendMessage(F.error("Shop", "Du hast nicht genug " + eco.currencyNamePlural() + " für diese Transaktion."));
        return false;
    }

    public boolean sell(Player player, int amount) {
        if (!this.purchaseEnabled) {
            player.sendMessage(F.error("Shop", "Der Shop kauft dieses Item nicht."));
            return false;
        }
        if (!UtilInv.hasItems(player.getInventory(), getItem(), amount)) {
            player.sendMessage(F.error("Shop", "Du hast nicht genug Items für diese Transaktion."));
            return false;
        }
        if (!this.shop.withdrawBalance(this.purchasePrice * amount)) {
            player.sendMessage(F.error("Shop", "Der Shop hat nicht genug Geld um dieses Item zu kaufen."));
            return false;
        }
        eco.depositPlayer((OfflinePlayer)player, this.purchasePrice * amount);
        UtilInv.removeItem(player.getInventory(), getItem(), amount);
        this.stock += amount;
        return true;
    }

    public void openStockInventory(Player player, BiConsumer<Player, Inventory> result) {
        Inventory inventory = Bukkit.createInventory((InventoryHolder)player, 54, "Warenbestand");
        ItemStack item = new ItemStack(this.item);
        item.setAmount(getStock());
        UtilInv.insert(inventory, item, false, null);
        player.openInventory(inventory);
        manager.registerEditor(player,this);
    }

    public String getPriceLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("§fPreis: §2");
        if (this.sellEnabled) {
            sb.append(this.sellPrice);
        } else {
            sb.append("§l§4-");
        }
        sb.append("§f | §b");
        if (this.purchaseEnabled) {
            sb.append(this.purchasePrice);
        } else {
            sb.append("§l§4-");
        }
        return sb.toString();
    }

    public void refill(int amount) {
        this.stock += amount;
    }


    public int getStock() {
        return this.stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getSellPrice() {
        return this.sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public double getPurchasePrice() {
        return this.purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public boolean isPurchaseEnabled() {
        return this.purchaseEnabled;
    }

    public void setPurchaseEnabled(boolean purchaseEnabled) {
        this.purchaseEnabled = purchaseEnabled;
    }

    public boolean isSellEnabled() {
        return this.sellEnabled;
    }

    public void setSellEnabled(boolean sellEnabled) {
        this.sellEnabled = sellEnabled;
    }

    public Shop getShop() {
        return this.shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public ItemStack getItem() {
        return this.item;
    }

    protected void inject(Shop shop) {
        this.shop = shop;
    }

    public static void setManager(ShopManager manager) {ShopContent.manager = manager;
    }
}
