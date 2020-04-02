package de.pauerbanane.core.addons.essentials.commands;


import java.util.Random;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.CommandHelp;
import de.pauerbanane.acf.CommandIssuer;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import org.bukkit.entity.Player;

@CommandAlias("weather")
@CommandPermission("command.weather")
@Description("Erlaubt es dir das Wetter zu ändern.")
public class WeatherCommand extends BaseCommand {

    @Default
    public void sendHelp(CommandIssuer issuer, CommandHelp help) {
        help.showHelp(issuer);
    }

    @Subcommand("sun|clear")
    public void setSun(Player issuer) {
        int duration = (300 + (new Random()).nextInt(600)) * 20;
        issuer.getWorld().setWeatherDuration(duration);
        issuer.getWorld().setThunderDuration(duration);
        issuer.getWorld().setThundering(false);
        issuer.getWorld().setStorm(false);
        issuer.sendMessage(F.main("Wetter", "In der Welt §6" + issuer.getWorld().getName() + " §7scheint nun die §6Sonne§7."));
    }

    @Subcommand("rain")
    public void setRaining(Player issuer) {
        int duration = (300 + (new Random()).nextInt(600)) * 20;
        issuer.getWorld().setWeatherDuration(duration);
        issuer.getWorld().setThunderDuration(duration);
        issuer.getWorld().setThundering(false);
        issuer.getWorld().setStorm(true);
        issuer.sendMessage(F.main("Wetter", "In der Welt  §6" + issuer.getWorld().getName() + " regnet §7es nun."));
    }

    @Subcommand("thunder|storm")
    public void setStorm(Player issuer) {
        int duration = (300 + (new Random()).nextInt(600)) * 20;
        issuer.getWorld().setWeatherDuration(duration);
        issuer.getWorld().setThunderDuration(duration);
        issuer.getWorld().setThundering(true);
        issuer.getWorld().setStorm(true);
        issuer.sendMessage(F.main("Wetter", "In der Welt §6" + issuer.getWorld().getName() + " stürmt §7es nun."));
    }
}