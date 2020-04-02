package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Description;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

@CommandAlias("nopickup|np")
@Description("Verhindert das aufsammeln von herumliegenden Items.")
@CommandPermission("command.nopickup")
public class NoPickupCommand extends BaseCommand {

    @Default
    public void disablePickup(Player sender) {
        if (sender.getCanPickupItems()) {
            sender.setCanPickupItems(false);
            sender.sendMessage(F.main("Info", "Du sammelst ab jetzt keine Items mehr auf."));
        } else {
            sender.setCanPickupItems(true);
            sender.sendMessage(F.main("Info", "Du kannst nun wieder Items aufsammeln."));
        }
    }
}