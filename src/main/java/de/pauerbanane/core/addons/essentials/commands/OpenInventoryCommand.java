package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandAlias("openinv|invsee|openinventory")
@CommandPermission("command.openinv")
public class OpenInventoryCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void openInv(Player sender, OnlinePlayer target) {
        sender.openInventory((Inventory)target.getPlayer().getInventory());
    }

}