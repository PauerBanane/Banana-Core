package de.pauerbanane.core.addons.ultrahardcore.commands;

import com.google.common.collect.Maps;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

@CommandAlias("event")
public class TopTenCommand extend s BaseCommand {

    @Subcommand("top")
    public void toplist(Player sender) {
        TreeMap<Integer, String> ranking = Maps.newTreeMap(Collections.reverseOrder());
        for(OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
            if(op.hasPlayedBefore()) {
                Iterator<Advancement> it = Bukkit.advancementIterator();
                Player player = op.getPlayer();
                int achieved  = 0;

                while (it.hasNext()) {
                    Advancement advancement = it.next();
                    if(player.getAdvancementProgress(advancement).isDone())
                        achieved += 1;
                }


            }
        }
    }

}
