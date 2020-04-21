package de.pauerbanane.core.addons.chairs.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.chairs.Chairs;
import org.bukkit.entity.Player;

@CommandAlias("sit")
public class SitCommand extends BaseCommand {

    private Chairs addon;

    public SitCommand(Chairs addon) {
        this.addon = addon;
    }

    @Default
    public void sit(Player sender) {
        if(sender.isFlying()) {
            sender.sendMessage(F.error("Sit", "Du kannst in der Luft nicht sitzen."));
            return;
        }
        addon.toggleSit(sender, null);
    }

}
