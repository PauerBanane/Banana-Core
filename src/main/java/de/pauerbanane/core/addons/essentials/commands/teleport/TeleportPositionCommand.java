package de.pauerbanane.core.addons.essentials.commands.teleport;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("tppos")
@CommandPermission("command.tppos")
public class TeleportPositionCommand extends BaseCommand {

    @Default
    @CommandCompletion("@nothing @nothing @nothing @worlds @players")
    @Syntax("<X> <Y> <Z> <Welt> [Spieler]")
    public void teleportPosition(CommandSender sender, int x, int y, int z, World world, @Optional OnlinePlayer player) {
        if (player == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(F.error("Dieser Command kann nur von einem Spieler ausgefwerden."));
                return;
            }
            Player p = (Player)sender;
            p.teleport(new Location(world, x, y, z));
            UtilPlayer.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT);
            return;
        }
        player.getPlayer().teleport(new Location(world, x, y, z));
        UtilPlayer.playSound(player.getPlayer(), Sound.ENTITY_ENDERMAN_TELEPORT);
        sender.sendMessage(F.main("Telepot", String.valueOf(F.name(player.getPlayer().getDisplayName())) + " wurde an die angegebene Position teleportiert."));
    }
}
