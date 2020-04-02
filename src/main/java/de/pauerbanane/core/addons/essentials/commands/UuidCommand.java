package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("uuid")
@CommandPermission("command.uuid")
public class UuidCommand extends BaseCommand {

    @Default
    public void getUUID(Player sender) {
        sender.sendMessage(F.main("Admin", "Deine UUID lautet:"));
        sender.sendMessage("§e" + sender.getUniqueId().toString());
    }

    @Default
    @CommandCompletion("@players")
    public void getFromOnlinePlayer(Player sender, OnlinePlayer target) {
        sender.sendMessage(F.main("Admin", "Die UUID von §e" + target.getPlayer().getName() + " §7lautet:"));
        sender.sendMessage("§e" + target.getPlayer().getUniqueId().toString());
    }

    @Default
    public void getFromOfflinePlayer(Player sender, OfflinePlayer target) {
        sender.sendMessage(F.main("Admin", "Die UUID von §e" + target.getName() + " §7lautet:"));
        sender.sendMessage("§e" + target.getUniqueId().toString());
    }

}
