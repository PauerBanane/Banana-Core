package de.pauerbanane.core.addons.essentials.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("spielzeit")
public class SpielzeitCommand extends BaseCommand {

    private long lastUpdate;

    private Gson gson;

    private TreeMap<Integer, String> ranking;

    public SpielzeitCommand() {
        this.gson = new Gson();
        this.lastUpdate = 0L;
        this.ranking = new TreeMap<>();
    }

    @Default
    public void onDefault(Player sender) {
        String playTime = UtilTime.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(sender.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20));
        sender.sendMessage(F.main("Spielzeit", "Deine gesamte Spielzeit beträgt §e" + playTime));
    }

    @Subcommand("top")
    public void top(final Player sender) {
        if(this.lastUpdate + TimeUnit.SECONDS.toMillis(10L) > System.currentTimeMillis()) {
            sendList(sender);
            return;
        }

        ranking = new TreeMap<>();
        File statsFolder = new File(((World) Bukkit.getWorlds().get(0)).getWorldFolder(), "stats");
        File[] files = statsFolder.listFiles();

        for(int i = 0; i < files.length; i++) {
            File file = files[i];
            try {
                JsonObject obj = (JsonObject)gson.fromJson(new FileReader(file), JsonObject.class);
                int time = obj.get("stats").getAsJsonObject().get("minecraft:custom").getAsJsonObject().get("minecraft:play_one_minute").getAsInt();
                OfflinePlayer op = Bukkit.getOfflinePlayer(UUID.fromString(file.getName().substring(0, file.getName().length() - 5)));
                ranking.put(Integer.valueOf(time), op.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sendList(sender);
    }

    private void sendList(Player player) {
        player.sendMessage("§7§m§l=========== §r§l Top 15 Spielzeit §7§m§l===========");
        player.sendMessage("");
        for(int i = 0; i < 15; i++) {
            int time = (int) ranking.keySet().toArray()[ranking.size() - (i + 1)];
            String name = ranking.get(time);
            String playTime = UtilTime.between(LocalDateTime.now(), LocalDateTime.now().plusSeconds(time / 20));
            player.sendMessage(ChatColor.GRAY + "#§6" + (i+1) + " §e" + name + " §7mit " + ChatColor.AQUA + playTime);
        }
        player.sendMessage("");
        player.sendMessage("§7§m§l========================================");
    }

}