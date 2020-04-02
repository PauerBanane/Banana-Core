package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@CommandAlias("nightvision|nv|nachtsicht")
@CommandPermission("command.nightvision")
@Description("Erlaubt es dir im Dunkeln zu sehen.")
public class NightvisionCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public void activateNightVision(Player sender, @Optional OnlinePlayer op) {
        if (op != null) {
            Player target = op.getPlayer();
            boolean mode = switchNightVision(target);
            sender.sendMessage(F.main("Nachtsicht", "Nachtsicht fSpieler " + F.name(target.getDisplayName()) + " wurde " + F.ctf(mode, "§2aktiviert", "§4deaktiviert")));
            target.sendMessage(F.main("Nachtsicht", String.valueOf(F.name(sender.getDisplayName())) + " hat Nachtsicht fdich " + F.ctf(mode, "§2aktiviert", "§4deaktiviert")));
            UtilPlayer.playSound(target, Sound.ENTITY_SPLASH_POTION_THROW, 0.5F, 0.9F);
            return;
        }
        sender.sendMessage(F.main("Nachtsicht", "Nachtsicht wurde " + F.ctf(switchNightVision(sender), "§2aktiviert", "§4deaktiviert")));
        UtilPlayer.playSound(sender, Sound.ENTITY_SPLASH_POTION_THROW, 0.5F, 0.9F);
    }

    private boolean switchNightVision(Player target) {
        if (target.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            target.removePotionEffect(PotionEffectType.NIGHT_VISION);
            return false;
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 2147483647, 1, false, false, true));
        return true;
    }
}