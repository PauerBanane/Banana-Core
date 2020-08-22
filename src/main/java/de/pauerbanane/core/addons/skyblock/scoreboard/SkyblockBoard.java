package de.pauerbanane.core.addons.skyblock.scoreboard;

import com.google.common.collect.Lists;
import de.pauerbanane.api.BananaAPI;
import de.pauerbanane.api.scoreboards.api.Entry;
import de.pauerbanane.api.scoreboards.api.EntryBuilder;
import de.pauerbanane.api.scoreboards.api.ScoreboardHandler;
import de.pauerbanane.api.util.DateTickFormat;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.data.RegionManager;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

public class SkyblockBoard implements ScoreboardHandler {

    private SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat time = new SimpleDateFormat("HH:mm");

    @Override
    public String getTitle(Player player) {
        return "§6§lSkyblock";
    }

    @Override
    public List<Entry> getEntries(Player player) {
        Date now = new Date();

        List<String> quest = Lists.newArrayList(PlaceholderAPI.setPlaceholders(player, "%quests_player_current_quest_names%").split("\n"));
        List<String> objective = Lists.newArrayList(PlaceholderAPI.setPlaceholders(player, "%quests_player_current_objectives_" + quest.get(0) + "%").split("\n"));

        String islandOwner = PlaceholderAPI.setPlaceholders(player, "%acidisland_visited_island_owner%");

        String icon = UtilTime.isMinecraftDay(player.getWorld().getTime()) ? "§e☀" : "§9☁";

        String timeformat = UtilTime.smoothDatetickFormat(player.getWorld().getTime());

        if (islandOwner.equals(player.getName()))
            islandOwner = "Deine Insel";

        String locationName = islandOwner.length() > 0 ? islandOwner : RegionManager.getInstance().getCurrentRegionName(player);

        EntryBuilder builder = new EntryBuilder()
                .blank()
                .next("§8" + date.format(now))
                .next("§7" + timeformat + " Uhr " + icon)
                .blank()
                .next("§7♢ §6" + locationName)
                .blank()
                .next("§7Geld: §e" + BananaCore.getEconomy().getBalance(player) + " " + BananaCore.getEconomy().currencyNamePlural())
                .blank();

        if (!quest.isEmpty() && quest.get(0).length() > 0) {
            builder.next("§7Quest §8(" + quest.size() + ")§7: §e" + quest.get(0));
            for (int i = 0; i < objective.size(); i++)
                builder.next("§8» §e" + objective.get(i));
        } else
            builder.next("§8» §7Keine Quests");

        builder.blank();

        return builder.build();
    }
}
