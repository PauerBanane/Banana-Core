package de.pauerbanane.core.addons.infos;

import com.google.common.collect.Lists;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Infos extends Addon implements Runnable {

    private FileLoader config;

    private ArrayList<List<String>> messages;

    private ArrayList<UUID> hiddenPlayers;

    private int counter = 0;

    @Override
    public void onEnable() {
        this.hiddenPlayers = Lists.newArrayList();

        loadConfig();

        registerCommand(new InfoCommand(this));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReload() {

    }

    private void loadConfig() {
        File file = new File(getAddonFolder(), "Infos.yml");
        if (!file.exists())
            UtilFile.copyResource(plugin.getResource("Infos.yml"), file);

        this.config = new FileLoader(getAddonFolder(), "Infos.yml");

        this.messages = Lists.newArrayList();
        for (String info : config.getKeys(false)) {
            messages.add(config.getStringList(info));
        }
    }

    @Override
    public void run() {
        List<String> messages = this.messages.get(counter);

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(!hiddenPlayers.contains(p.getUniqueId()))
                for(String s : messages) {
                    p.sendMessage("§a§l>§c> §r" + s);
                }
        }

        if((counter + 1) >= this.messages.size()) {
            counter = 0;
        } else
            counter++;
    }

    public ArrayList<UUID> getHiddenPlayers() {
        return hiddenPlayers;
    }
}
