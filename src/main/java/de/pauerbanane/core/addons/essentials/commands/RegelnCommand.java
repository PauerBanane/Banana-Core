package de.pauerbanane.core.addons.essentials.commands;

import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.essentials.Essentials;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("regeln")
public class RegelnCommand extends BaseCommand {

    private List<String> messages;

    public RegelnCommand(Essentials addon) {
        messages = Lists.newArrayList();
        if(!addon.getPlugin().getConfig().isSet("rulesMessage")) {
            addon.getPlugin().getConfig().set("rulesMessage", Lists.newArrayList("§7Klicke auf den Link, um unsere Regeln zu lesen:", "§ehttps://knickscraft.de/pages/gesetz/"));
            addon.getPlugin().saveConfig();
        }

        messages = addon.getPlugin().getConfig().getStringList("rulesMessage");
    }

    @Default
    public void send(Player sender) {
        messages.forEach(message -> sender.sendMessage(F.main("Regeln", message)));
    }

}
