package de.pauerbanane.core.addons.playershop;

import com.google.common.collect.Sets;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.UUIDFetcher;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.playershop.events.PlayerShopRentEvent;
import de.pauerbanane.core.addons.playershop.events.PlayerShopResetEvent;
import de.pauerbanane.core.addons.playershop.gui.ShopOverviewGUI;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Shop implements ConfigurationSerializable {

    private LinkedHashSet<ShopContent> contents;

    private final String plotRegion;

    private String shopName;

    private final Location location;

    private boolean active;

    private Villager.Profession profession;

    private int shopSlots = 27;

    private double balance = 0.0D;

    private UUID owner;

    private UUID entity;

    private String lastKnownNickname;

    public Shop(Location location, String plotID) {
        this.contents = Sets.newLinkedHashSet();
        this.location = location;
        this.plotRegion = plotID;
    }

    public void addContent(ShopContent content) {
        if(contents.size() >= shopSlots) {
            BananaCore.getInstance().getLogger().warning("Couldn't load any more ShopContent for Shop " + plotRegion);
            return;
        }
        content.inject(this);
        contents.add(content);
    }

    public boolean withdrawBalance(double amount) {
        if (this.balance - amount < 0.0D)
            return false;
        this.balance -= amount;
        return true;
    }

    public void depositBalance(double amount) {
        this.balance += amount;
    }

    public void openShopInterface(Player player) {
        if (this.active) {
            SmartInventory.builder().provider((InventoryProvider)new ShopOverviewGUI(this)).title("Spielershop").size(6).build().open(player);
        } else {
            UtilPlayer.playSound(player, Sound.ENTITY_VILLAGER_NO);
            player.sendActionBar("§cDieser Shop ist nicht aktiv.");
        }

    }

    public Optional<ShopContent> addContent(ItemStack item) {
        if (this.contents.size() >= this.shopSlots)
            return Optional.empty();
        ShopContent content = new ShopContent(item);
        content.inject(this);
        content.setStock(item.getAmount());
        this.contents.add(content);
        return Optional.of(content);
    }

    public void deleteContent(ShopContent content) {
        this.contents.remove(content);
    }

    public void update(Plot plot) {
        if (this.lastKnownNickname == null && hasOwner())
            UUIDFetcher.getName(this.owner, name -> name.ifPresent(this::setLastKnownNickname));
        if (plot.hasOwner() && !this.active) {
            rentShop(plot.getOwner());
            return;
        }
        if (this.active)
            resetShop();
    }

    private void resetShop() {
        PlayerShop.getInstance().getStorageManager().saveOnReset(this);
        new PlayerShopResetEvent(this).callEvent();
        this.active = false;
        this.owner = null;
        this.contents = Sets.newLinkedHashSet();
        this.balance = 0.0D;
        this.shopName = "Shop";
        this.lastKnownNickname = null;
        updateEntity();
    }

    private void rentShop(UUID owner) {
        this.active = true;
        this.owner = owner;
        updateEntity();
        new PlayerShopRentEvent(this).callEvent();
    }

    public void updateEntity() {
        if(owner != null)
            this.active = true;
        Entity ent = this.location.getWorld().getEntity(this.entity);
        if (ent == null) {
            Bukkit.getLogger().severe("Shop-Villager konnte nicht gefunden werden...");
            ent = spawnEntity();
        }
        Villager villager = (Villager)ent;
        if(this.owner != null)
            this.lastKnownNickname = Bukkit.getOfflinePlayer(this.owner).getName();
        if (this.isActive()) {
            if(this.shopName == null || this.shopName.equalsIgnoreCase("§2Shop")) {
                villager.setCustomName("§7Shop von §2" + this.getLastKnownNickname());
            } else
                villager.setCustomName(ChatColor.translateAlternateColorCodes('&', this.shopName));

            villager.setVillagerExperience(2);
        } else {
            villager.setProfession(Villager.Profession.FISHERMAN);
            villager.setCustomName("§8Inaktiver Shop");
            villager.setVillagerExperience(2);
        }
    }

    public Villager spawnEntity() {
        World world = this.location.getWorld();
        Villager villager = (Villager)world.spawnEntity(this.location, EntityType.VILLAGER);
        villager.setAI(false);
        villager.setInvulnerable(true);
        villager.setProfession(Villager.Profession.FISHERMAN);
        villager.setCustomNameVisible(true);
        villager.setVillagerExperience(10);
        villager.setSilent(true);
        villager.getPersistentDataContainer().set(ShopManager.key, PersistentDataType.STRING, getPlotRegion());
        setEntity(villager.getUniqueId());
        return villager;
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("plotRegion", plotRegion);
        result.put("location", UtilLoc.serialize(location));
        result.put("shopSlots", shopSlots);
        result.put("balance", balance);

        if(owner != null) {
            result.put("owner", owner.toString());
        } else
            result.put("owner", null);
        if(entity != null) {
            result.put("entity", entity.toString());
        } else
            result.put("entity", null);
        result.put("shopName", shopName);
        result.put("profession", profession.toString());
        result.put("active", active);
        result.put("lastKnownNickName", lastKnownNickname);

        int i = 0;
        for(ShopContent content : contents) {
            result.put("content." + i, content);
            i++;
        }

        return result;
    }

    public static Shop deserialize(Map<String, Object> args) {
        String plotRegion = (String) args.get("plotRegion");
        Location location = UtilLoc.deserialize((String) args.get("location"));
        Shop shop = new Shop(location, plotRegion);
        shop.setShopSlots(Integer.valueOf((Integer) args.get("shopSlots")));
        shop.setBalance(Double.valueOf((Double) args.get("balance")));

        String ownerID = (String) args.get("owner");
        if(ownerID == null) {
            shop.setOwner(null);
        } else
            shop.setOwner(UUID.fromString(ownerID));

        String entityID = (String) args.get("entity");
        if(entityID == null) {
            shop.setEntity(null);
        } else
            shop.setEntity(UUID.fromString(entityID));

        if(args.containsKey("profession"))
            shop.setProfession(Villager.Profession.valueOf((String) args.get("profession")));
        if(args.containsKey("active"))
            shop.setActive((Boolean) args.get("active"));
        if(args.containsKey("lastKnownNickName"))
            shop.setLastKnownNickname((String) args.get("lastKnownNickName"));
        if(args.containsKey("shopName"))
            shop.setShopName((String) args.get("shopName"));

        boolean iterate = true;
        int i = 0;
        while (iterate) {
            if(args.containsKey("content." + i)) {
                ShopContent content = (ShopContent) args.get("content." + i);
                shop.addContent(content);
                i++;
            } else
                iterate = false;
        }
        BananaCore.getInstance().getLogger().info("Loaded " + i + " contents for shop " + plotRegion);

        return shop;
    }

    public double getBalance() {
        return this.balance;
    }

    public boolean hasOwner() {
        return (this.owner != null);
    }

    public UUID getOwner() {
        return this.owner;
    }

    public void setOwner(UUID uuid) {
        this.owner = uuid;
    }

    public UUID getEntity() {
        return this.entity;
    }

    public void setShopSlots(int shopSlots) {
        this.shopSlots = shopSlots;
    }

    public void setEntity(UUID entity) {
        this.entity = entity;
    }

    public String getShopName() {
        return (this.shopName == null) ? "Shop" : this.shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
        updateEntity();
    }

    public Villager.Profession getProfession() {
        return this.profession;
    }

    public void setProfession(Villager.Profession profession) {
        this.profession = profession;
    }

    public String getLastKnownNickname() {
        return this.lastKnownNickname;
    }

    public void setLastKnownNickname(String lastKnownNickname) {
        this.lastKnownNickname = lastKnownNickname;
    }

    public int getShopSlots() {
        return this.shopSlots;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Location getLocation() {
        return this.location;
    }

    public String getPlotRegion() {
        return this.plotRegion;
    }

    public boolean isActive() {
        return this.active;
    }

    public LinkedHashSet<ShopContent> getShopContent() {
        return this.contents;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    protected void injectContentHolder() {
        this.contents.forEach(content -> content.inject(this));
    }
}
