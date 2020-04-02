package de.pauerbanane.core.addons.essentials.commands.teleport.home;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@CommandAlias("home")
public class HomeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@userhomes")
    public void homeCommand(Player player, @Single @Optional String home) {
        HomeData homes = CorePlayer.get(player.getUniqueId()).getData(HomeData.class);
        if (home == null) {
            if (homes.getHomes().size() == 1) {
                player.teleport(homes.getHomes().values().stream().findFirst().get(), PlayerTeleportEvent.TeleportCause.COMMAND);
                UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
                return;
            }
            player.sendMessage(F.main("Home", "Deine Homepunkte (" + homes.getHomes().size() + "/" + homes.getMaxHomes() + "):"));
            List<String> homelist = new ArrayList<>(homes.getHomes().keySet());
            Collections.sort(homelist, String.CASE_INSENSITIVE_ORDER);
            player.sendMessage(F.main("Home", F.format(homelist, ", ", "Keine")));
            return;
        }
        if (!homes.hasHome(home)) {
            player.sendMessage(F.error("Der angegebende Homepunkt existiert nicht."));
            return;
        }
        player.sendMessage(F.main("Home", "Teleportiere nach " + home + "..."));
        player.teleport(homes.getHome(home), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
    }

    @Subcommand("admin")
    @CommandPermission("command.home.others")
    @Syntax("<Spieler> [Home]")
    public void homeAdmin(Player player, OfflinePlayer target, @Single @Optional String home) {
        CorePlayer cp = CorePlayer.get(target.getUniqueId());
        HomeData homes = cp.getData(HomeData.class);
        if (home == null) {
            player.sendMessage(F.main("Home", "Homes von " + F.name(target.getName()) + "(" + homes.getHomes().size() + "/" + homes.getMaxHomes() + "):"));
            List<String> homelist = new ArrayList<>(homes.getHomes().keySet());
            Collections.sort(homelist, String.CASE_INSENSITIVE_ORDER);
            player.sendMessage(F.main("Home", F.format(homelist, ", ", "Keine")));
            return;
        }
        if (!homes.hasHome(home)) {
            player.sendMessage(F.error("Der angegebende Homepunkt existiert nicht."));
            return;
        }
        player.sendMessage(F.main("Home", "Teleportiere nach " + home + "..."));
        player.teleport(homes.getHome(home), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
    }
}
