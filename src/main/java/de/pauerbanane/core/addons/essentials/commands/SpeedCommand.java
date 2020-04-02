package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

@CommandAlias("speed")
@CommandPermission("command.speed")
public class SpeedCommand extends BaseCommand {

    @Default
    @CommandCompletion("@range:5 @players")
    public void onDefault(final Player sender, final int speed, @Optional final OnlinePlayer op) {
        if (op == null) {
            float f = speed / 10.0F;
            if (sender.isFlying()) {
                sender.setFlySpeed(f);
                sender.sendMessage(F.main("Speed", "Deine Fluggeschwindigkeit wurde auf §6" + speed + " §7gesetzt."));
                return;
            }
            sender.setWalkSpeed(f);
            sender.sendMessage(F.main("Speed", "Deine Laufgeschwindigkeit wurde auf §6" + speed + " §7gesetzt."));
            return;
        }
        if (!sender.hasPermission("kccore.speed.others")) {
            sender.sendMessage(F.main("Speed", "§cDazu hast du keine Rechte!"));
            return;
        }

        float s = speed / 10.0F;
        Player target = op.getPlayer();
        if (target.isFlying()) {
            target.setFlySpeed(s);
            sender.sendMessage(F.main("Speed", "Die Fluggeschwindigkeit von §6" + target.getName() + " §7wurde auf §6" + speed + " §7gesetzt."));
            target.sendMessage(F.main("Speed", "Deine Fluggeschwindigkeit wurde auf §6" + speed + " §7gesetzt."));
            return;
        }
        target.setWalkSpeed(s);
        sender.sendMessage(F.main("Speed", "Die Laufgeschwindigkeit von §6" + target.getName() + " §7wurde auf §6" + speed + " §7gesetzt."));
        target.sendMessage(F.main("Speed", "Deine Laufgeschwindigkeit wurde auf §6" + speed + " §7gesetzt."));
    }

}