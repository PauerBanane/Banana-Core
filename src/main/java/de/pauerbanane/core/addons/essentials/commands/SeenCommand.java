package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("seen")
public class SeenCommand extends BaseCommand {

    private Addon addon;

    public SeenCommand(Addon addon) {
        this.addon = addon;
    }

    @Default
    public void onDefault(Player sender, OfflinePlayer offlinePlayer) {
        if (offlinePlayer.isOnline()) {
            sender.sendMessage(F.main("Seen", "§e" + offlinePlayer.getName() + " §7ist gerade online."));
            return;
        }
        sender.sendMessage(F.main("Seen", "§e" + offlinePlayer.getName() + " §7war zuletzt vor " + UtilTime.getElapsedTime(offlinePlayer.getLastSeen()) + " online."));
    }

}