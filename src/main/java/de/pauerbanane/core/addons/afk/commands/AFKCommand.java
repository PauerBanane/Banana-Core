package de.pauerbanane.core.addons.afk.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Optional;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import de.pauerbanane.core.addons.afk.AFK;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("afk")
public class AFKCommand extends BaseCommand {

    private AFK addon;

    public AFKCommand(AFK addon) {
        this.addon = addon;
    }

    @Default
    public void onDefault(Player sender, @Optional String reason) {
        if (addon.isAfk(sender)) {
            Bukkit.broadcastMessage(F.main("AFK", "§e" + sender.getName() + " §7ist nach " + ChatColor.LIGHT_PURPLE + UtilTime.getElapsedTime(addon.getAfkTime().get(sender.getUniqueId())) + " §7wieder da."));
            addon.setAfk(sender, false);
        } else {
            addon.setAfk(sender, true);
            if (reason != null && !reason.isEmpty()) {
                Bukkit.broadcastMessage(F.main("AFK", "§e" + sender.getName() + " §7ist nun AFK. Grund: " + ChatColor.LIGHT_PURPLE + reason));
            } else {
                Bukkit.broadcastMessage(F.main("AFK", "§e" + sender.getName() + " §7ist nun AFK."));
            }
        }
    }



}