package de.pauerbanane.core.addons.chat;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PluginMessageChat extends Addon implements Listener {

    private String channel;

    @Override
    public void onEnable() {
        this.channel = "system:chat";
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, channel);

        registerListener(this);
    }

    @Override
    public void onDisable() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, channel);
    }

    @Override
    public void onReload() {

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleChatInput(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        e.setCancelled(true);

        Player player = e.getPlayer();

        if(player.hasPermission("chat.color"))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));

        Bukkit.getOnlinePlayers().forEach(p -> {
            if(e.getMessage().contains(p.getName()))
                UtilPlayer.playSound(p, Sound.BLOCK_NOTE_BLOCK_HARP);
        });

        sendChatMessage(player, e.getMessage());
    }

    private void sendChatMessage(Player player, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(message);

        player.sendPluginMessage(plugin, channel, out.toByteArray());
    }

}
