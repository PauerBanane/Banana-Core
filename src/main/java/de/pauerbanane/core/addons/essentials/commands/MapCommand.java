package de.pauerbanane.core.addons.essentials.commands;

import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.addons.essentials.Essentials;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("map|karte")
public class MapCommand extends BaseCommand {

    private Essentials addon;

    private FileLoader config;

    private List<String> messages;

    public MapCommand(Essentials addon) {
        this.addon = addon;
        this.config = addon.getConfig();
        this.messages = Lists.newArrayList();

        if(!config.isSet("map-link")) {
            this.messages.add("§7Klicke auf den Link, um unsere aktuellen §eWeltkarten §7zu sehen:");
            this.messages.add("§ehttps://map.knickscraft.de/");
            config.set("map-link", messages).save();
        } else {
            this.messages = config.getStringList("map-link");
        }
    }

    @Default
    public void send(Player sender) {
        messages.forEach(message -> sender.sendMessage(F.main("Karte", message)));
    }

}
