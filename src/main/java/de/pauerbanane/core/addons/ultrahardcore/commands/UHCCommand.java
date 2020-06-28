package de.pauerbanane.core.addons.ultrahardcore.commands;

import com.google.common.collect.Maps;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.core.addons.ultrahardcore.UltraHardcore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("uhc|ultrahardcore")
public class UHCCommand extends BaseCommand {

    @Subcommand("top")
    public void top(Player sender) {
        HashMap<UUID, Integer> map = Maps.newHashMap();
        for(String uuid : UltraHardcore.getConfig().getKeys(false)) {
            UUID id = UUID.fromString(uuid);
            int achieved = 0;
            if(Bukkit.getPlayer(id) != null && Bukkit.getPlayer(id).isOnline()) {
                Player player = Bukkit.getPlayer(id);
                Iterator<Advancement> it = Bukkit.advancementIterator();
                while (it.hasNext()) {
                    Advancement advancement = it.next();
                    if(player.getAdvancementProgress(advancement).isDone())
                        achieved += 1;
                }
            } else
                achieved = UltraHardcore.getConfig().getConfig().getInt(uuid + ".advancements", 0);

            map.put(id, achieved);
        }

        LinkedHashMap<UUID, Integer> sortedMap = map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e2, LinkedHashMap::new));


        sender.sendMessage("§7§m§l=========== §r§l Top 10 im Event §7§m§l===========");
        sender.sendMessage("");

        int length = sortedMap.size() >= 10 ? 10 : sortedMap.size();
        int i = 0;
        for(Iterator<UUID> it = sortedMap.keySet().iterator(); i < length; i++) {
            UUID id = it.next();
            String name = Bukkit.getOfflinePlayer(id).getName();
            Integer amount = sortedMap.get(id);
            sender.sendMessage(ChatColor.GRAY + "#§6" + (i+1) + " §e" + name + " §7mit " + ChatColor.AQUA + amount + " Fortschritten");
        }

        sender.sendMessage("");
        sender.sendMessage("§7§m§l========================================");
    }

}
