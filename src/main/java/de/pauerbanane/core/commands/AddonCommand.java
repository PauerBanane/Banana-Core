package de.pauerbanane.core.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.addons.AddonManager;
import de.pauerbanane.api.util.F;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("addon")
@CommandPermission("command.addon")
public class AddonCommand extends BaseCommand {

    private static AddonManager manager = AddonManager.getInstance();

    @Default
    public void sendAddonList(Player sender) {
        manager.getAddons().forEach(addon -> sender.sendMessage(ChatColor.BLUE + addon.getName() + "§7: " + F.ctf(addon.isEnabled(), "§2AKTIVIERT", "§4DEAKTIVIERT")));
    }

    @Subcommand("enable")
    @CommandCompletion("@addon")
    public void enable(Player sender, Addon addon) {
        if(addon.isEnabled()) {
            sender.sendMessage(F.error("Addon","Dieses Addon ist bereits aktiviert."));
            return;
        }

        manager.enableAddon(addon);
        sender.sendMessage(F.main("Addon", "Das Addon wurde aktiviert."));
    }

    @Subcommand("disable")
    @CommandCompletion("@addon")
    public void disable(Player sender, Addon addon) {
        if(!addon.isEnabled()) {
            sender.sendMessage(F.error("Addon","Dieses Addon ist bereits deaktiviert."));
            return;
        }

        manager.disableAddon(addon);
        sender.sendMessage(F.main("Addon", "Das Addon wurde deaktiviert."));
    }

}
