package de.pauerbanane.core.addons.essentials.commands.teleport.home;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Single;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.entity.Player;

@CommandAlias("delhome")
public class DelhomeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@userhomes")
    public void deleteHome(Player player, @Single String home) {
        CorePlayer cp = CorePlayer.get(player.getUniqueId());
        HomeData homes = cp.getData(HomeData.class);
        if (homes.getTotalHomes() == 0) {
            player.sendMessage(F.error("Du hast keine Homepunkte."));
            return;
        }
        if (!homes.deleteHome(home)) {
            player.sendMessage(F.error("Der angegebende Homepunkt existiert nicht."));
            return;
        }
        player.sendMessage(F.main("Home", "Der angegebende Homepunkt wurde gelöscht."));
    }
}
