package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

@CommandAlias("heal|heilen|food")
@CommandPermission("command.heal")
public class HealCommand extends BaseCommand {

    @Default
    public void onDefault(Player sender) {
        sender.setHealth(20);
        sender.setFoodLevel(20);
        sender.sendMessage(F.main("Heal", "Du wurdest geheilt."));
    }

    @Default
    @CommandPermission("command.heal.others")
    @CommandCompletion("@players")
    public void onFlyOthers(Player sender, OnlinePlayer t) {
        Player target = t.getPlayer();

        target.setHealth(20);
        target.setFoodLevel(20);
        target.sendMessage(F.main("Heal", "Du wurdest geheilt."));
        sender.sendMessage(F.main("Heal", "Du hast §6" + target.getName() + " §7geheilt."));
    }

}