package de.pauerbanane.core.addons.essentials.commands.teleport;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

@CommandAlias("tpall")
@CommandPermission("command.tphere")
public class  TeleportAllCommand extends BaseCommand {

    @Default
    public void teleportAll(Player sender) {
        sender.sendMessage(F.main("Teleport", "Teleportiere alle Spieler zu dir..."));
        Bukkit.getOnlinePlayers().forEach(target -> {
            if(target != sender)
                target.teleport((Entity)sender, PlayerTeleportEvent.TeleportCause.COMMAND);
                target.sendMessage(F.main("Teleport", "Du wurdest zu " + F.name(sender.getDisplayName()) + " teleportiert."));
        });
    }
}