package de.pauerbanane.core.addons.essentials.commands.spawn;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("spawn|markt")
public class SpawnCommand extends BaseCommand {

    @Default
    public void spawn(Player sender) {
        Location spawn = new FileLoader("plugins/Banana-Core/Addons/spawn.yml").getLocation("spawn");
        if(spawn == null) {
            sender.sendMessage(F.error("Spawn", "Es wurde noch kein Spawnpunkt festgelegt."));
            return;
        }
        sender.teleport(spawn);
    }
}
