package de.pauerbanane.core.addons.ultrahardcore.data;

import de.pauerbanane.api.data.PlayerData;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.addons.ultrahardcore.UltraHardcore;
import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class UHCData extends PlayerData {

    private FileLoader config = UltraHardcore.getConfig();

    private int lifes;

    @Override
    public void initialize() {

    }

    @Override
    public void saveData(YamlConfiguration yamlConfiguration) {
        Player player = Bukkit.getPlayer(getOwner());
        config.set(getOwner().toString() + ".lifes", lifes);
        config.save();
        if(!player.isOnline()) return;
        int achieved = 0;

        Iterator<Advancement> it = Bukkit.advancementIterator();
        while (it.hasNext()) {
            Advancement advancement = it.next();
            if(player.getAdvancementProgress(advancement).isDone())
                achieved += 1;
        }

        config.set(getOwner().toString(), achieved);
    }

    @Override
    public void loadData(YamlConfiguration yamlConfiguration) {
        if(!config.isSet(getOwner().toString())) {
            config.set(getOwner().toString() + ".advancements", 0);
            config.save();
        }
        if(!config.isSet(getOwner().toString() + ".lifes")) {
            config.set(getOwner().toString() + ".lifes", 3);
            config.save();
        }

        this.lifes = config.getConfig().getInt(getOwner().toString() + ".lifes", 3);
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }
}
