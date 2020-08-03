package de.pauerbanane.core.addons.vote;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import it.unimi.dsi.fastutil.Hash;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.UUID;

public class VoteManager implements PluginMessageListener {

    private String channel;

    private VoteAddon addon;

    private BananaCore plugin;

    private static HashMap<UUID, Integer> votes;

    public VoteManager(VoteAddon addon) {
        this.addon = addon;
        this.plugin = (BananaCore) addon.getPlugin();
        this.votes = Maps.newHashMap();
        this.channel = "system:vote";

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        System.out.println("Votes received");
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String[] uuids = in.readUTF().split(";");
        String[] amounts = in.readUTF().split(";");
        System.out.println(uuids);
        System.out.println(amounts);
        votes.clear();

        for(int i = 0; i < uuids.length; i++) {
            UUID uuid = UUID.fromString(uuids[i]);

            if(uuid == null || !UtilMath.isInt(amounts[i])) continue;

            int amount = Integer.parseInt(amounts[i]);

            votes.put(uuid, amount);
        }
    }

    public static int getVotes(Player player) {
        return votes.containsKey(player.getUniqueId()) ? votes.get(player.getUniqueId()) : 0;
    }
}
