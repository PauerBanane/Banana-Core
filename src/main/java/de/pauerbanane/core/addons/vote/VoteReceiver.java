package de.pauerbanane.core.addons.vote;

import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.HashMap;
import java.util.UUID;

public class VoteReceiver implements PluginMessageListener {

    private String channel;

    private VoteChestManager manager;

    private BananaCore plugin;

    public VoteReceiver(VoteChestManager manager) {
        this.manager = manager;
        this.plugin = BananaCore.getInstance();
        this.channel = "system:vote";

        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, channel, this);
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
        String uuidAsString = in.readUTF();

        UUID uuid = UUID.fromString(uuidAsString);
        if (uuid == null) {
            plugin.getLogger().warning("Failed to init Vote received by PluginMessage: " + uuidAsString + " (UUID)");
            return;
        }

        plugin.getLogger().info("Received vote by PluginMessage from " + uuidAsString);
        manager.checkIfVotetargetReached();
    }
}