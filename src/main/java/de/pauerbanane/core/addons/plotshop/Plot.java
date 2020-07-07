package de.pauerbanane.core.addons.plotshop;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.util.UtilTime;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@SerializableAs("plot")
public class Plot implements ConfigurationSerializable {

    private PlotShop addon;

    private final String regionID;

    private final ProtectedRegion region;

    private final String world;

    private final PlotGroup plotGroup;

    private double price;

    private UUID owner;

    private LocalDateTime purchaseDate = LocalDateTime.of(1337, 9, 9, 9, 9);

    private LocalDateTime expireDate = LocalDateTime.of(1337, 9, 9, 9, 9);


    public Plot(String regionID, ProtectedRegion region, World world, PlotGroup group) {
        this.regionID = regionID;
        this.region = region;
        this.world = world.getName();
        this.plotGroup = group;

        this.addon = PlotShop.getInstance();
    }

    public boolean isExpired() {
        if(this.owner == null)
            return false;
        return LocalDateTime.now().isAfter(this.expireDate);
    }

    public boolean isAboutToExpire() {
        if(this.owner == null)
            return false;
        long hours = LocalDateTime.now().until(this.expireDate, ChronoUnit.HOURS);
        return (hours < 48);
    }

    public String getPurchaseDateFormatted() {
        if(this.purchaseDate == null)
            return "Nicht verkauft";
        ZonedDateTime zone = this.purchaseDate.atZone(ZoneId.systemDefault());
        return UtilTime.when(zone.toInstant().toEpochMilli());
    }

    public String getExpireDateFormatted() {
        if (!isRentable())
            return "Niemals";
        if (isExpired())
            return "Abgelaufen";
        ZonedDateTime zone = this.expireDate.atZone(ZoneId.systemDefault());
        return UtilTime.when(zone.toInstant().toEpochMilli());
    }

    public boolean isRentable() {
        return this.plotGroup.getRentDays() > 0;
    }


    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("regionID", this.regionID);
        result.put("world", this.world);
        result.put("group", this.plotGroup.getGroupID());
        result.put("price", Double.valueOf(this.price));
        if(this.owner != null) {
            result.put("owner", this.owner.toString());
            ZonedDateTime zdt = this.purchaseDate.atZone(ZoneId.systemDefault());
            result.put("purchaseDate", Long.valueOf(zdt.toInstant().toEpochMilli()));
            ZonedDateTime ezdt = this.expireDate.atZone(ZoneId.systemDefault());
            result.put("expireDate", Long.valueOf(ezdt.toInstant().toEpochMilli()));
        }
        return result;
    }

    public static Plot deserialize(Map<String, Object> args) {
        String id = (String) args.get("regionID");
        World world = Bukkit.getWorld((String) args.get("world"));
        String groupID = (String) args.get("group");
        PlotGroup group = PlotShop.getInstance().getManager().getPlotGroup(groupID);
        if(group == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load Plot " + id + "! The defined group is not longer valid!");
            return null;
        }
        System.out.println(id);
        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world)).getRegion(id);
        Plot plot = new Plot(id, region, world, group);
        plot.setPrice((Double) args.get("price"));
        if(args.containsKey("owner"))
            plot.setOwner(UUID.fromString(args.get("owner").toString()));
        if (args.containsKey("purchaseDate")) {
            long date = ((Long)args.get("purchaseDate")).longValue();
            LocalDateTime ldtt = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault());
            plot.setPurchaseDate(ldtt);
        }
        if (args.containsKey("expireDate")) {
            long date = ((Long)args.get("expireDate")).longValue();
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault());
            plot.setExpireDate(ldt);
        }
        return plot;
    }

    public boolean hasOwner() {
        return this.owner != null;
    }

    public int getExpireHours() {
        return (int)LocalDateTime.now().until(getExpireDate(), ChronoUnit.HOURS);
    }

    public void setAddon(PlotShop addon) {
        this.addon = addon;
    }

    public UUID getOwner() {
        return owner;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getExpireDate() {
        return expireDate;
    }

    public LocalDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public PlotGroup getPlotGroup() {
        return plotGroup;
    }

    public PlotShop getAddon() {
        return addon;
    }

    public ProtectedRegion getRegion() {
        return region;
    }

    public String getRegionID() {
        return regionID;
    }

    public String getWorld() {
        return world;
    }

    public void setExpireDate(LocalDateTime expireDate) {
        this.expireDate = expireDate;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPurchaseDate(LocalDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
