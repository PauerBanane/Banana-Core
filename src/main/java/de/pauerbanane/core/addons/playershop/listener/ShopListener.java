package de.pauerbanane.core.addons.playershop.listener;

import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.core.addons.playershop.Shop;
import de.pauerbanane.core.addons.playershop.ShopManager;
import de.pauerbanane.core.addons.playershop.oldContent.ShopStorageManager;
import de.pauerbanane.core.addons.plotshop.events.PlotExpireEvent;
import de.pauerbanane.core.addons.plotshop.events.PlotRentEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.persistence.PersistentDataType;

public class ShopListener implements Listener {

    private final ShopManager manager;

    private final ShopStorageManager storageManager;

    private final String title = "Warenbestand";

    public ShopListener(ShopManager manager, ShopStorageManager storageManager) {
        this.manager = manager;
        this.storageManager = storageManager;
    }

    @EventHandler
    public void villagerDamage(EntityDamageEvent e) {
        for(Shop shop : manager.getRegisteredShops().values()) {
            //if(shop.getEntity().equals(e.getEntity().getUniqueId()))
              //e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRegionExpire(PlotExpireEvent event) {
        if (!this.manager.isShopRegion(event.getPlot().getRegionID()))
            return;
        Shop shop = this.manager.getShopByRegion(event.getPlot().getRegionID());
        shop.update(event.getPlot());
    }

    @EventHandler
    public void onRent(PlotRentEvent event) {
        if (!this.manager.isShopRegion(event.getPlot().getRegionID()))
            return;
        Shop shop = this.manager.getShopByRegion(event.getPlot().getRegionID());
        shop.update(event.getPlot());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getView().getTitle().equals("Warenbestand")) {
            Player player = (Player) event.getPlayer();
        /*BiConsumer<Player, Inventory> consumer = manager.getContentConsumer(player);
        this.manager.getContentConsumer(player).accept(player, event.getInventory());*/
            manager.unregisterEditor(player, event.getInventory());
        } else if(event.getView().getTitle().equals("Alter Warenbestand")) {
            Player player = (Player) event.getPlayer();
            storageManager.unregisterStoragedEditor(player, event.getInventory());
        } else
            return;
    }

    @EventHandler
    public void blockVillagerShops(InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.MERCHANT) {
            Entity holder = (Entity)event.getInventory().getHolder();
            if (holder instanceof Villager && this.manager.isShop((Villager)holder)) {
                event.setCancelled(true);
                String shopID = (String)holder.getPersistentDataContainer().get(ShopManager.key, PersistentDataType.STRING);
                if (shopID == null) {
                    Bukkit.getLogger().warning("Failed to open shop for entity: " + holder.getUniqueId().toString() + " at " + UtilLoc.getLogString(holder.getLocation()));
                    return;
                }
                this.manager.getShop(shopID).openShopInterface((Player)event.getPlayer());
            }
        }
    }
}
