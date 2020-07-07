package de.pauerbanane.core.addons.chat;

import com.google.common.collect.Lists;
import de.pauerbanane.core.BananaCore;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;

public class SignListener implements Listener {

    private BananaCore plugin;

    public SignListener(BananaCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleSigns(SignChangeEvent e) {
        if(!e.getPlayer().hasPermission("chat.color")) return;
        String[] lines = e.getLines();
        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String formatted = ChatColor.translateAlternateColorCodes('&', line);
            e.setLine(i, formatted);
        }

        Sign sign = (Sign) e.getBlock().getState();
        sign.setEditable(true);
        sign.update();
    }


}
