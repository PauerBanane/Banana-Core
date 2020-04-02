package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import org.bukkit.entity.Player;

@CommandAlias("wb|workbench|werkbank")
@CommandPermission("command.workbench")
public class WorkbenchCommand extends BaseCommand {

    @Default
    public void onDefault(Player sender) {
        sender.openWorkbench(sender.getLocation(), true);
    }

}
