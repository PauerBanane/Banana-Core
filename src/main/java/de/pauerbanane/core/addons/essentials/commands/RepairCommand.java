package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilGear;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

@CommandAlias("repair|fix")
@CommandPermission("command.repair")
public class RepairCommand extends BaseCommand {

    @Default
    public void repair(Player sender) {
        if (UtilGear.isRepairable(sender.getInventory().getItemInMainHand())) {
            ItemStack item = sender.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (((Damageable)meta).getDamage() == 0) {
                sender.sendMessage(F.main("Repair", "Dieses Item ist nicht beschädigt."));
                return;
            }
            ((Damageable)meta).setDamage(0);
            item.setItemMeta(meta);
            sender.sendMessage(F.main("Repair", "Du hast das Item repariert."));
            return;
        }
        sender.sendMessage(F.error("Dieses Item lsich nicht reparieren!"));
    }

    @Subcommand("all")
    @CommandPermission("ct.repair.all")
    public void repairAll(Player sender) {
        byte b;
        int i;
        ItemStack[] arrayOfItemStack;
        for (i = (arrayOfItemStack = sender.getInventory().getContents()).length, b = 0; b < i; ) {
            ItemStack item = arrayOfItemStack[b];
            ItemMeta meta = item.getItemMeta();
            if (UtilGear.isRepairable(item) && ((Damageable)meta).getDamage() > 0) {
                sender.sendMessage(F.main("Repair", "Du hast das Item repariert."));
                ((Damageable)meta).setDamage(0);
                item.setItemMeta(meta);
            }
            b++;
        }
    }
}