package de.pauerbanane.core.addons.discovery.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.discovery.Discovery;
import de.pauerbanane.core.addons.discovery.DiscoveryAddon;
import de.pauerbanane.core.addons.discovery.data.DiscoveryData;
import de.pauerbanane.core.addons.discovery.events.DiscoveryUnlockEvent;
import de.pauerbanane.core.addons.discovery.gui.DiscoveryListGUI;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("discovery")
public class DiscoveryCommand extends BaseCommand {

    private DiscoveryAddon addon;

    public DiscoveryCommand(DiscoveryAddon addon) {
        this.addon = addon;
    }

    @Default
    public void onDefault(Player sender) {
        SmartInventory.builder().provider(new DiscoveryListGUI(addon)).title("§eDiscovery").size(3).build().open(sender);
    }

    @Subcommand("create")
    @CommandPermission("command.discovery")
    @CommandCompletion("@region @nothing")
    public void create(Player sender, ProtectedRegion region, @Single String name) {
        if (addon.registerDiscovery(new Discovery(name, sender.getWorld().getName(), region.getId(), null, null))) {
            sender.sendMessage(F.main("Discovery", "Du hast die Discovery §e" + name + " §7erfolgreich erstellt."));
        } else
            sender.sendMessage(F.error("Discovery", "Es existiert bereits eine Discovery mit diesem Namen."));
    }

    @Subcommand("remove")
    @CommandPermission("command.discovery")
    @CommandCompletion("@discovery @nothing")
    public void remove(Player sender, Discovery discovery) {
        addon.unregisterDiscovery(discovery);
        sender.sendMessage(F.main("Discovery", "Die Discovery wurde entfernt."));
    }

    @Subcommand("seticon")
    @CommandPermission("command.discovery")
    @CommandCompletion("@discovery @material @nothing")
    public void setIcon(Player sender, Discovery discovery, Material icon) {
        discovery.setIcon(icon);
        sender.sendMessage(F.main("Discovery", "Du hast das Icon zu §e" + icon.toString() + " §7geändert."));
    }

    @Subcommand("complete")
    @CommandPermission("command.discovery")
    @CommandCompletion("@players @discovery")
    public void complete(Player sender, OnlinePlayer target, Discovery discovery) {
        DiscoveryData data = CorePlayer.get(target.getPlayer().getUniqueId()).getData(DiscoveryData.class);
        if (!data.hasAchievedDiscovery(discovery)) {
            data.addDiscovery(discovery);
            new DiscoveryUnlockEvent(discovery, target.getPlayer()).callEvent();
        } else
            sender.sendMessage(F.error("Discovery", "Der Spieler hat diese Discovery bereits gefunden."));
    }

    @Subcommand("revoke")
    @CommandPermission("command.discovery")
    @CommandCompletion("@players @discovery")
    public void revoke(Player sender, OnlinePlayer target, Discovery discovery) {
        DiscoveryData data = CorePlayer.get(target.getPlayer().getUniqueId()).getData(DiscoveryData.class);
        if (data.hasAchievedDiscovery(discovery)) {
            data.removeDiscovery(discovery);
            sender.sendMessage(F.main("Discovery", "Die Discovery §a" + discovery.getName() + " §7wurde für §e" + target.getPlayer().getName() + " §4entfernt§7."));
        } else
            sender.sendMessage(F.error("Discovery", "Der Spieler hat diese Discovery noch nicht gefunden."));
    }

}
