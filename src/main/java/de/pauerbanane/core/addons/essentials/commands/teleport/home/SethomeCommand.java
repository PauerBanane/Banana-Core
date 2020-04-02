package de.pauerbanane.core.addons.essentials.commands.teleport.home;


import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Single;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.entity.Player;

@CommandAlias("sethome")
public class SethomeCommand extends BaseCommand {

    @Default
    public void setHome(Player player, @Single String home) {
        CorePlayer cp = CorePlayer.get(player.getUniqueId());
        HomeData homes = cp.getData(HomeData.class);
        if (home.contains("-") || home.contains(";") || home.contains(".")) {
            player.sendMessage(F.error("Dieser Name enthält ungültige Zeichen!"));
            return;
        }
        if (homes.getTotalHomes() >= homes.getMaxHomes()) {
            player.sendMessage(F.error("Du hast bereits die maximale anzahl an Homepunkten erreicht."));
            return;
        }
        homes.addHome(home, player.getLocation());
        player.sendMessage(F.main("Home", "Du hast einen neuen Homepunkt angelegt."));
    }
}