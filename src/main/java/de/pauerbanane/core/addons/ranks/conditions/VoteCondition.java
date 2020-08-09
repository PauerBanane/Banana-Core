package de.pauerbanane.core.addons.ranks.conditions;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.math.MathUtils;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.sql.DatabaseManager;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("voteCondition")
public class VoteCondition extends RankCondition {

    private int requiredVotes;

    public VoteCondition(int requiredVotes) {
        super.type = TYPE.VOTE_CONDITION;
        this.requiredVotes = requiredVotes;
    }

    @Override
    public boolean conditionAchieved(Player player) {
        return DatabaseManager.getInstance().getVotes(player.getUniqueId()) >= requiredVotes;
    }

    @Override
    public List<String> requirementsAsLore(Player player) {
        List<String> lore = Lists.newArrayList();
        lore.add("§7Votes: §e" + DatabaseManager.getInstance().getVotes(player.getUniqueId()) + "§8/§e" + requiredVotes);

        return lore;
    }


    @Override
    public Map<String, Object> serialize() {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();

        result.put("requiredVotes", requiredVotes);

        return result;
    }

    public static VoteCondition deserialize(Map<String, Object> args) {
        int amount = (int) args.getOrDefault("requiredVotes", 999);

        return new VoteCondition(amount);
    }
}
