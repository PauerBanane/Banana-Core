package de.pauerbanane.core.data.conditions;

import com.google.common.collect.Lists;
import org.bukkit.Statistic;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("playtimeCondition")
public class PlaytimeCondition extends Condition {

    private int requiredHours;

    public PlaytimeCondition(int requiredHours) {
        super.type = Type.PLAYTIME_CONDITION;
        this.requiredHours = requiredHours;
    }


    @Override
    public boolean conditionAchieved(Player player) {
        int playedSeconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int playedMinutes = playedSeconds / 60;
        int playedHours = playedMinutes / 60;

        return playedHours >= requiredHours;
    }

    @Override
    public List<String> requirementsAsLore(Player player) {
        int playedSeconds = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        int playedMinutes = playedSeconds / 60;
        int playedHours = playedMinutes / 60;

        List<String> lore = Lists.newArrayList();
        lore.add("§7Spielzeit: §e" + playedHours + "§8/§e" + requiredHours + " Stunden");

        return lore;
    }
    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("requiredHours", requiredHours);

        return result;
    }

    public static PlaytimeCondition deserialize(Map<String, Object> args) {
        int amount = (int) args.getOrDefault("requiredHours", 2000);

        return new PlaytimeCondition(amount);
    }

}
