package de.pauerbanane.core.addons.votifier.listener;

import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.votifier.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class VoteListener implements Listener {
    private final BananaCore plugin;

    private List<String> messages;

    public VoteListener(BananaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onVote(VotifierEvent event) {
        this.plugin.getLogger().info(event.getVote().getUsername() + " has voted on " + event.getVote().getServiceName());
        this.plugin.getServer().broadcastMessage("");
        this.plugin.getServer().broadcastMessage("§e" + event.getVote().getUsername() + " §7hat auf unserem Server gevotet.");
        this.plugin.getServer().broadcastMessage("§7Hast du heute schon §egevotet§7? §2/vote");
        this.plugin.getServer().broadcastMessage("");
    }
}