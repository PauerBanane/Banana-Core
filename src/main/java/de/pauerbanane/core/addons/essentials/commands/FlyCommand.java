package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("fly")
@CommandPermission("command.fly")
public class FlyCommand extends BaseCommand {

    @Default
    public void fly(Player sender) {
        if(sender.getGameMode() == GameMode.CREATIVE) {
            sender.sendMessage(F.error("Fly", "Diesen Befehl kannst du im Kreativ-Modus nicht benutzen."));
            return;
        }

        if(toggleFlight(sender)) {
            sender.sendMessage(F.main("Fly", "Flugmodus wurde §2aktiviert§7."));
        } else
            sender.sendMessage(F.main("Fly", "Flugmodus wurde §cdeaktviert§7."));
    }

    @Default
    @CommandPermission("command.fly.others")
    @CommandCompletion("@players")
    public void enable(Player sender, OnlinePlayer t) {
        Player target = t.getPlayer();

        if(target.getGameMode() == GameMode.CREATIVE) {
            sender.sendMessage(F.error("Fly", "Der Spieler ist bereits im Kreativ-Modus."));
            return;
        }

        if(toggleFlight(target)) {
            sender.sendMessage(F.main("Fly", "Flugmodus wurde für §e" + target.getName() + " §2aktiviert§7."));
            target.sendMessage(F.main("Fly", "Flugmodus wurde §2aktiviert§7."));
        } else {
            sender.sendMessage(F.main("Fly", "Flugmodus wurde für §e" + target.getName() + " §cdeaktiviert§7."));
            target.sendMessage(F.main("Fly", "Flugmodus wurde §cdeaktiviert§7."));
        }
    }

    private boolean toggleFlight(Player target) {
        if (target.getAllowFlight()) {
            target.setAllowFlight(false);
            target.setFlying(false);
            return false;
        }
        target.setAllowFlight(true);
        target.setFlying(true);
        return true;
    }

}
