package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("gamemode|gm")
@CommandPermission("command.gamemode")
public class GamemodeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@gamemode")
    public void gamemode(final Player sender, final GameMode mode, @Optional final OnlinePlayer op) {

        if (op != null && sender.hasPermission("command.gamemode.others")) {
            final Player target = op.getPlayer();
            target.sendMessage(F.main("Admin", "Dein Spielmodus wurde in " + F.name(StringUtils.capitalize(mode.name().toLowerCase())) + " geändert!"));
            sender.sendMessage(F.main("Admin", "Du hast den Spielmodus von " + F.name(target.getDisplayName()) + " in " + F.name(StringUtils.capitalize(mode.name().toLowerCase())) + " geändert!"));
            op.getPlayer().setGameMode(mode);
        } else {
            sender.sendMessage(F.main("Admin", "Dein Spielmodus wurde in " + F.name(StringUtils.capitalize(mode.name().toLowerCase())) + " geändert!"));
            sender.setGameMode(mode);
        }
    }
}