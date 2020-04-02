package de.pauerbanane.core.addons.essentials.commands.teleport;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@CommandAlias("tphere")
@CommandPermission("command.tphere")
public class TeleportHereCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void teleportHere(Player issuer, OnlinePlayer target) {
        issuer.sendMessage(F.main("Teleport", "Teleportiere " + F.name(target.getPlayer().getDisplayName()) + " zu dir."));
        target.getPlayer().teleport(issuer.getLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(target.getPlayer(), Sound.ENTITY_ENDERMAN_TELEPORT);
        target.getPlayer().sendMessage(F.main("Teleport", "Du wurdest zu " + F.name(issuer.getDisplayName()) + "teleportiert."));
    }
}