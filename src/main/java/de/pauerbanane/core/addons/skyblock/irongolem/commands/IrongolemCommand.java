package de.pauerbanane.core.addons.skyblock.irongolem.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.skyblock.irongolem.IronGolemManager;
import org.bukkit.entity.Player;

@CommandAlias("irongolem")
@CommandPermission("command.skyblock")
public class IrongolemCommand extends BaseCommand {

    private IronGolemManager manager;

    public IrongolemCommand(IronGolemManager manager) {
        this.manager = manager;
    }

    @Default
    public void def(Player sender) {
        sender.sendMessage(F.main("Skyblock", "Enabled: §e" + manager.isAbort()));
        sender.sendMessage(F.main("Skyblock", "Chance: §e" + manager.getPercentage() + "%"));
    }

    @Subcommand("enable")
    @CommandCompletion("@boolean")
    public void enable(Player sender, boolean tf) {
        manager.setAbort(tf);
        sender.sendMessage(F.main("Skyblock", "IronGolemHandler: §e" + tf));
    }

    @Subcommand("setpercentage")
    public void set(Player sender, int p) {
        manager.setPercentage(p);
        sender.sendMessage(F.main("Skyblock", "Chance: §e" + p + "§7%"));
    }

}
