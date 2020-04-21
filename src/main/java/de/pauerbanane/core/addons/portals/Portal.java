package de.pauerbanane.core.addons.portals;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

public class Portal implements ConfigurationSerializable {

    private Portals.PortalType portalType;

    private String name;

    private String portalRegion;

    private World portalWorld;

    private Location destinationLocation;

    private String server;

    private String description;

    public Portal(String name, String description, Portals.PortalType portalType, String portalRegion, World portalWorld, Location destinationLocation, String server) {
        if(portalType == Portals.PortalType.WORLDPORTAL && destinationLocation == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load Worldportal - no destination World defined");
            return;
        }
        if(portalType == Portals.PortalType.SERVERPORTAL && server == null) {
            BananaCore.getInstance().getLogger().warning("Failed to load Serverportal - no destination Server defined");
            return;
        }

        this.name = name;
        this.description = description;
        this.portalType = portalType;
        this.portalRegion = portalRegion;
        this.portalWorld = portalWorld;
        this.destinationLocation = destinationLocation;
        this.server = server;
    }

    public void teleport(Player player) {
        if(portalType == Portals.PortalType.WORLDPORTAL) {
            player.teleport(destinationLocation);
        } else if(portalType == Portals.PortalType.SERVERPORTAL) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(BananaCore.getInstance(), "BungeeCord", out.toByteArray());
        }
    }

    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("type", portalType.toString());
        result.put("name", name);
        result.put("description", description);
        result.put("region", portalRegion);
        result.put("world", portalWorld.getName());
        if(portalType == Portals.PortalType.SERVERPORTAL) {
            result.put("server", server);
        } else if(portalType == Portals.PortalType.WORLDPORTAL) {
            result.put("destination", UtilLoc.serialize(destinationLocation));
        }

        return result;
    }

    public static Portal deserialize(Map<String, Object> args) {
        Portals.PortalType portalType = Portals.PortalType.valueOf((String) args.get("type"));
        String name = (String) args.get("name");
        String description = (String) args.get("description");
        String portalRegion = (String) args.get("region");
        World world = Bukkit.getWorld((String) args.get("world"));
        if(portalType == Portals.PortalType.SERVERPORTAL) {
            String server = (String) args.get("server");
            System.out.println("return");
            return new Portal(name, description, portalType, portalRegion, world,null, server);
        } else if(portalType == Portals.PortalType.WORLDPORTAL) {
            Location location = UtilLoc.deserialize((String) args.get("destination"));
            return new Portal(name, description, portalType, portalRegion, world, location, null);
        } else {
            BananaCore.getInstance().getLogger().warning("Failed to deserialize Portal - no valid PortalType given");
            return null;
        }
    }


    public String getPortalRegion() {
        return portalRegion;
    }

    public Portals.PortalType getPortalType() {
        return portalType;
    }

    public String getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public World getPortalWorld() {
        return portalWorld;
    }

    public String getDescription() {
        return description;
    }
}
