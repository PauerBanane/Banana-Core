package de.pauerbanane.core.addons.essentials.commands.tpa;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("tpa")
public class TpaCommand extends BaseCommand {

    private final TeleportRequestManager manager;

    public TpaCommand(TeleportRequestManager manager) {
        this.manager = manager;
    }

    @Default
    @CommandCompletion("@players @nothing")
    @CommandPermission("command.tpa")
    public void sendRequest(Player player, OnlinePlayer target) {
        this.manager.addRequest(player, target.getPlayer(), TeleportRequestManager.TeleportRequestType.TELEPORT_TO);
        player.sendMessage(F.main("Teleport", "Anfrage an " + target.getPlayer().getName() + " wurde gesendet."));
        target.getPlayer().sendMessage(F.main("Teleport", "Du hast eine neue Teleportanfrage von §e" + player.getDisplayName() + "§7."));
    }

    @Subcommand("accept")
    @CommandCompletion("@players")
    public void acceptRequest(Player player, OnlinePlayer target) {
        if (this.manager.getActiveRequests(player).isEmpty()) {
            player.sendMessage("Keine aktiven Anfragen.");
            return;
        }
        if (!this.manager.getActiveRequests(player).contains(target.getPlayer().getUniqueId())) {
            player.sendMessage(F.main("Teleport", "Du hast keine Anfrage von §e" + F.name(target.getPlayer().getDisplayName() + "§7.")));
            return;
        }
        this.manager.acceptRequest(player, target.getPlayer().getUniqueId());
    }

    @Subcommand("deny")
    @CommandCompletion("@players")
    public void denyRequest(Player player, String targetID) {
        if (this.manager.getActiveRequests(player).isEmpty()) {
            player.sendMessage(F.main("Teleport", "Du hast keine aktiven Teleportanfragen."));
            return;
        }
        UUID target = UUID.fromString(targetID);
        if (target == null)
            return;
        this.manager.denyRequest(player, target);
    }

    @Subcommand("list")
    public void listRequests(Player player) {
        player.sendMessage(F.main("Teleport", "Liste deiner Teleportanfragen:"));
        if (this.manager.getActiveRequests(player).isEmpty()) {
            player.sendMessage(F.main("Teleport", "Du hast keine aktiven Teleportanfragen."));
            return;
        }
        this.manager.getActiveRequests(player).forEach(id -> {
            Player t = Bukkit.getPlayer(id);
            if (t != null)
                player.sendMessage(t.getDisplayName());
        });
    }
}