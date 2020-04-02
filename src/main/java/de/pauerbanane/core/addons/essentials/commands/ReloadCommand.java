package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.CommandIssuer;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.BananaCore;
import org.bukkit.Bukkit;

@CommandAlias("banana")
@CommandPermission("command.banana.reload")
public class ReloadCommand extends BaseCommand {

    @Subcommand("reload")
    public void reload(CommandIssuer sender) {
        sender.sendMessage(F.main("Admin", "Plugin wird neu geladen..."));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "plugman reload " + BananaCore.getInstance().getDescription().getName());
    }

}
