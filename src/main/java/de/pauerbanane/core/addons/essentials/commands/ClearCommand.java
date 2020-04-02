package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

@CommandAlias("clear|ci")
@CommandPermission("command.clearinventory")
public class ClearCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void clearInventory(Player sender, @Optional OnlinePlayer target) {
        if (target == null) {
            sender.getInventory().clear();
            sender.sendMessage(F.main("Admin", "Dein Inventar wurde geleert!"));
        } else {
            target.getPlayer().getInventory().clear();
            target.getPlayer().sendMessage(String.valueOf(F.main("Admin", "Dein Inventar wurde von " + F.name(sender.getDisplayName()))) + " geleert!");
        }
    }
}