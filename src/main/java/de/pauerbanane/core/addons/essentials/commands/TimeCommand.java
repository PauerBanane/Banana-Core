package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.DateTickFormat;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@CommandAlias("time|settime")
@CommandPermission("command.time")
@Description("Erlaubt es dir die Zeit zu ändern.")
public class TimeCommand extends BaseCommand {

        @Default
        public void setTimeCommand(Player issuer, int time) {
            setTime(issuer, time);
        }

        @Subcommand("day")
        public void setDay(Player issuer) {
            setTime(issuer, 1000);
        }

        @Subcommand("night")
        public void setNight(Player issuer) {
            setTime(issuer, 14000);
        }

        private void setTime(Player issuer, int time) {
            if (time < 0 || time > 24000) {
                issuer.sendMessage(F.error(String.valueOf(time) + " muss zwischen 0 und 24000 sein."));
                return;
            }
            String worldname = issuer.getWorld().getName();
            issuer.getWorld().setTime(time);
            String timeformat = DateTickFormat.format24(issuer.getWorld().getTime());
            Bukkit.broadcastMessage("§7Zeit in Welt §e" + worldname + " §7wurde von §6" + issuer.getName() + " §7auf §e" + timeformat + " §7gesetzt.");
            UtilPlayer.playSound(issuer, Sound.AMBIENT_UNDERWATER_LOOP_ADDITIONS_ULTRA_RARE);
        }
}