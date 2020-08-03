package de.pauerbanane.core.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("customitem")
@CommandPermission("command.customitem")
public class CustomItemCommand extends BaseCommand {

    @Default
    @CommandCompletion("@material @nothing")
    public void get(Player sender, Material material, String name, @Optional int modelData) {
        ItemStack item = new ItemBuilder(material).name(ChatColor.translateAlternateColorCodes('&', name)).setModelData(modelData).build();
        sender.getInventory().addItem(item);
        sender.sendMessage(F.main("Item", "Das Item wurde erstellt."));
    }

}
