package de.pauerbanane.core.addons.essentials.commands.teleport;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@CommandAlias("tp|teleport")
@CommandPermission("command.tp")
public class TeleportCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players @players")
    public void teleport(Player sender, OnlinePlayer target, @Optional OnlinePlayer op) {
        if (op != null) {
            sender.sendMessage(F.main("Teleport", "Teleportiere " + F.name(target.getPlayer().getDisplayName()) + " zu " + F.name(op.getPlayer().getDisplayName())));
            UtilPlayer.playSound(target.getPlayer(), Sound.ENTITY_ENDERMAN_TELEPORT);
            target.getPlayer().teleport((Entity)op.getPlayer(), PlayerTeleportEvent.TeleportCause.COMMAND);
            return;
        }

        sender.sendMessage(F.main("Teleport", "Teleportiere zu " + F.name(target.getPlayer().getDisplayName())));
        sender.teleport((Entity)target.getPlayer(), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(sender, Sound.ENTITY_ENDERMAN_TELEPORT);
    }
}