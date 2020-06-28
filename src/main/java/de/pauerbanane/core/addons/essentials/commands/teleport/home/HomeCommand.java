package de.pauerbanane.core.addons.essentials.commands.teleport.home;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@CommandAlias("home")
public class HomeCommand extends BaseCommand {

    @Default
    @CommandCompletion("@userhomes")
    public void homeCommand(Player player, @Single @Optional String home) {
        HomeData homes = CorePlayer.get(player.getUniqueId()).getData(HomeData.class);
        if (home == null) {
            if (homes.getHomes().size() == 1) {
                player.teleport(homes.getHomes().values().stream().findFirst().get(), PlayerTeleportEvent.TeleportCause.COMMAND);
                UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
                return;
            }
            player.sendMessage(F.main("Home", "Deine Homepunkte (" + homes.getHomes().size() + "/" + homes.getMaxHomes() + "):"));
            List<String> homelist = new ArrayList<>(homes.getHomes().keySet());
            Collections.sort(homelist, String.CASE_INSENSITIVE_ORDER);
            player.sendMessage(F.main("Home", F.format(homelist, ", ", "Keine")));
            return;
        }
        if (!homes.hasHome(home)) {
            player.sendMessage(F.error("Der angegebende Homepunkt existiert nicht."));
            return;
        }
        player.sendMessage(F.main("Home", "Teleportiere nach " + home + "..."));
        player.teleport(homes.getHome(home), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
    }

    @Subcommand("admin")
    @CommandPermission("command.home.others")
    @CommandCompletion("@players @nothing")
    @Syntax("<Spieler> [Home]")
    public void homeAdmin(Player player, OnlinePlayer target, @Single @Optional String home) {
        CorePlayer cp = CorePlayer.get(target.getPlayer().getUniqueId());
        HomeData homes = cp.getData(HomeData.class);
        if (home == null) {
            player.sendMessage(F.main("Home", "Homes von " + F.name(target.getPlayer().getName()) + "(" + homes.getHomes().size() + "/" + homes.getMaxHomes() + "):"));
            List<String> homelist = new ArrayList<>(homes.getHomes().keySet());
            Collections.sort(homelist, String.CASE_INSENSITIVE_ORDER);
            player.sendMessage(F.main("Home", F.format(homelist, ", ", "Keine")));
            return;
        }
        if (!homes.hasHome(home)) {
            player.sendMessage(F.error("Der angegebende Homepunkt existiert nicht."));
            return;
        }
        player.sendMessage(F.main("Home", "Teleportiere nach " + home + "..."));
        player.teleport(homes.getHome(home), PlayerTeleportEvent.TeleportCause.COMMAND);
        UtilPlayer.playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
    }

    @Subcommand("merge")
    @CommandPermission("command.home.merge")
    public void merge(Player sender) {
        if(!sender.getName().equalsIgnoreCase("PauerBanane")) {
            sender.sendMessage(F.error("Home", "Dieser Befehl kann nur von PauerBanane ausgeführt werden!"));
            return;
        }

        File old = new File("plugins/Banana-Core/playerdata/");
        File[] data = old.listFiles();

        for(int i = 0; i < data.length; i++) {
            File f = data[i];
            if(!f.getName().contains("players") && UUID.fromString(f.getName().replace(".yml", "")) != null) {
                FileLoader config = new FileLoader(f.getParentFile(), f.getName());
                ConfigurationSection section = config.getConfigurationSection("homes");
                if (section != null && section.getKeys(false).size() > 0) {
                    for (String home : section.getKeys(false)) {
                        System.out.println("Loading home: " + home);
                        String serializedHome = String.valueOf(config.getString("homes." + home)).replace(",", ";");

                        config.set("homes." + home, serializedHome);
                        config.save();
                    }
                }
            }
        }
        System.out.println("Finish");
    }
}
