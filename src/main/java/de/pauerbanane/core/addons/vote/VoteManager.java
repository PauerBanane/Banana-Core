package de.pauerbanane.core.addons.vote;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.sql.DatabaseManager;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class VoteManager {

    private String channel;

    private VoteAddon addon;

    private BananaCore plugin;

    private static HashMap<UUID, Integer> votes;

    private static DatabaseManager databaseManager;

    public VoteManager(VoteAddon addon) {
        this.addon = addon;
        this.plugin = (BananaCore) addon.getPlugin();
        this.votes = Maps.newHashMap();
        this.databaseManager = DatabaseManager.getInstance();
    }

    public static int getVotes(Player player) {
        return databaseManager.getVotes(player.getUniqueId());
    }
}
