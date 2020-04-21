package de.pauerbanane.core.addons.portals.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.portals.Portal;
import de.pauerbanane.core.addons.portals.Portals;
import org.bukkit.World;
import org.bukkit.entity.Player;

@CommandAlias("portal")
@CommandPermission("command.portal")
public class PortalCommand extends BaseCommand {

    private Portals addon;

    public PortalCommand(Portals addon) {
        this.addon = addon;
    }

    @Default
    public void info(Player sender) {
        sender.sendMessage(F.main("Portal", "Portale: §e" + addon.getPortals().keySet()));
    }

    @Subcommand("create worldportal")
    @CommandCompletion("@nothing @nothing @region @worlds @worlds")
    @Syntax("<Portal-Typ> <Name> <Beschreibung> <Region> <Portal-Welt> <Ziel-Welt>")
    public void create(Player sender, String name, String description, String regionID, World sourceWorld, World destinationWorld) {
        if(!WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sourceWorld)).hasRegion(regionID)) {
            sender.sendMessage(F.error("Portal", "Es existiert keine Region mit dieser ID."));
            return;
        }

        Portal portal = new Portal(name, description, Portals.PortalType.WORLDPORTAL, regionID, sourceWorld, destinationWorld.getSpawnLocation(), null);
        if(!addon.registerPortal(portal)) {
            sender.sendMessage(F.error("Portal", "Dieses Portal existiert bereits."));
            return;
        }

        sender.sendMessage(F.main("Portal", "Das Portal wurde erstellt."));
    }

    @Subcommand("create serverportal")
    @CommandCompletion("@nothing @nothing @region @worlds @server")
    @Syntax("<Portal-Typ> <Name> <Beschreibung> <Region> <Portal-Welt> <Ziel-Server>")
    public void create(Player sender, String name, String description, String regionID, World sourceWorld, String server) {
        if(!WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sourceWorld)).hasRegion(regionID)) {
            sender.sendMessage(F.error("Portal", "Es existiert keine Region mit dieser ID."));
            return;
        }

        Portal portal = new Portal(name, description, Portals.PortalType.SERVERPORTAL, regionID, sourceWorld, null, server);
        if(!addon.registerPortal(portal)) {
            sender.sendMessage(F.error("Portal", "Dieses Portal existiert bereits."));
            return;
        }

        sender.sendMessage(F.main("Portal", "Das Portal wurde erstellt."));
    }

    @Subcommand("remove")
    @CommandCompletion("@portal")
    public void remove(Player sender, Portal portal) {
        addon.removePortal(portal);
        sender.sendMessage(F.main("Portal", "Das Portal wurde entfernt."));
    }

    @Subcommand("setlocation")
    @CommandCompletion("@portal")
    public void setLocation(Player sender, Portal portal) {
        portal.setDestinationLocation(sender.getLocation());
        sender.sendMessage(F.main("Portal", "Das Ziel des Portals wurde neu gesetzt."));
    }

}
