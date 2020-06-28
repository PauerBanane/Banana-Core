package de.pauerbanane.core.addons.essentials.commands.spawn;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import org.bukkit.entity.Player;

@CommandAlias("setspawn")
@CommandPermission("command.setspawn")
public class SetspawnCommand extends BaseCommand {

    @Default
    public void setspawn(Player sender) {
        new FileLoader("plugins/Banana-Core/Addons", "spawn.yml").saveLocation("spawn", sender.getLocation());
        sender.getWorld().setSpawnLocation(sender.getLocation());
        sender.sendMessage(F.main("Spawn", "Du hast den Spawn neu gesetzt."));
    }

}
