package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilInv;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("hat")
@CommandPermission("command.hat")
@Description("Setzt dir einen Block auf den Kopf.")
public class HatCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    @Conditions("iteminhand")
    public void hatTarget(Player sender, @Optional OnlinePlayer op) {
        if (op != null) {
            if (!sender.hasPermission("command.hat.others")) {
                sender.sendMessage(F.error(Bukkit.getPermissionMessage()));
                return;
            }
            Player target = op.getPlayer();
            if (target.getInventory().getHelmet() != null)
                UtilInv.insert(target, target.getInventory().getHelmet(), true);
            String str = sender.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
            sender.sendMessage(F.main("Hat", "Du hast " + F.name(String.valueOf(target.getDisplayName()) + " ") + F.name(str) + " auf den Kopf gesetzt."));
            target.sendMessage(F.main("Hat", String.valueOf(F.name(sender.getDisplayName())) + " hat dir " + F.name(str) + " auf den Kopf gesetzt."));
            target.getInventory().setHelmet(sender.getInventory().getItemInMainHand());
            return;
        }
        if (sender.getInventory().getHelmet() != null)
            UtilInv.insert(sender, sender.getInventory().getHelmet());
        String itemname = sender.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        sender.sendMessage(F.main("Hat", "Du hast " + F.name(itemname) + " auf den Kopf gesetzt."));
        sender.getInventory().setHelmet(sender.getInventory().getItemInMainHand());
        if (sender.getGameMode() != GameMode.CREATIVE)
            sender.getInventory().setItemInMainHand(null);
    }
}
