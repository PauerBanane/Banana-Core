package de.pauerbanane.core.addons.infos;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;


public class InfoCommand extends BaseCommand {

    private Infos addon;
    private ArrayList<UUID> hiddenPlayers;

    public InfoCommand(Infos addon) {
        this.addon = addon;
        this.hiddenPlayers = addon.getHiddenPlayers();
    }

    @Subcommand("hide")
    public void hide(Player sender) {
        if(hiddenPlayers.contains(sender.getUniqueId())) {
            sender.sendMessage(F.error("Infos", "Du hast die Infos bereits ausgeblendet."));
            return;
        }

        hiddenPlayers.add(sender.getUniqueId());
        sender.sendMessage(F.main("Infos", "Du bekommst nun keine Infos mehr."));
    }

    @Subcommand("show")
    public void show(Player sender) {
        if(!hiddenPlayers.contains(sender.getUniqueId())) {
            sender.sendMessage(F.error("Infos", "Du bekommst die Infos bereits."));
            return;
        }

        hiddenPlayers.remove(sender.getUniqueId());
        sender.sendMessage(F.main("Infos", "Die Infos werden dir wieder angezeigt."));
    }

}