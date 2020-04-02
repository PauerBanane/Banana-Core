package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

@CommandAlias("echest|enderchest")
@CommandPermission("command.enderchest")
public class EnderchestCommand extends BaseCommand {

    @Default
    public void onDefault(Player sender) {
        Inventory enderchest = sender.getEnderChest();
        sender.openInventory(enderchest);
    }

    @Default
    @CommandPermission("kccore.enderchest.others")
    @CommandCompletion("@players")
    public void others(Player sender, OnlinePlayer op) {
        Inventory enderchest = op.getPlayer().getEnderChest();
        sender.openInventory(enderchest);
    }

}