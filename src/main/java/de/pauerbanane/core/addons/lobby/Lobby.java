package de.pauerbanane.core.addons.lobby;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.lobby.server.ServerInterfaceManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Lobby extends Addon implements Listener {

    private FileLoader config;

    private ArrayList<UUID> rulesNotAccepted;

    private String firstJoinMessage;

    private ServerInterfaceManager serverManager;

    @Override
    public void onEnable() {
        if(!plugin.getServerName().equalsIgnoreCase("Lobby")) {
            plugin.getLogger().severe("Failed to load Lobby-Addon - Servername is not set to 'Lobby'");
            return;
        }
        rulesNotAccepted = Lists.newArrayList();
        loadConfig();
        this.serverManager = new ServerInterfaceManager(this);



        registerCommand(new RulesAcceptCommand(this));

        registerListener(this);
        registerListener(serverManager);
    }

    @Override
    public void onDisable() {
        List<String> notAcceptedIds = Lists.newArrayList();
        rulesNotAccepted.forEach(id -> notAcceptedIds.add(id.toString()));

        config.set("rulesNotAccepted", notAcceptedIds);
        config.save();
    }

    private void loadConfig() {
        config = new FileLoader(getAddonFolder() + "Rules.yml");

        if(!config.isSet("firstJoinMessage")) {
            config.set("firstJoinMessage", "§dWillkommen auf KnicksCraft §e%player%§d!");
        }

        config.save();

        if(config.isSet("rulesNotAccepted")) {
            List<String> ids = config.getStringList("rulesNotAccepted");
            ids.forEach(id -> rulesNotAccepted.add(UUID.fromString(id)));
        }

        firstJoinMessage = config.getString("firstJoinMessage");
    }

    @EventHandler
    public void handleHunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleItemDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleItemPickup(PlayerAttemptPickupItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void handleAir(EntityAirChangeEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void handleDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void handleLobbyJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        p.getInventory().clear();
        p.getInventory().setItem(0, serverManager.getServerInterfaceItem());

        p.setHealth(20);
        p.setFoodLevel(20);
    }

    @EventHandler
    public void handleMove(PlayerMoveEvent e) {
        if (!hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(F.error("Regeln", "Du musst zuerst die Regeln akzeptieren. §e/accept"));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void handleChat(AsyncPlayerChatEvent e) {
        if (!hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(F.error("Regeln", "Du musst zuerst die Regeln akzeptieren. §e/accept"));
            e.setCancelled(true);
        }
    }



    @EventHandler(priority = EventPriority.HIGH)
    public void handlePlayerJoin(PlayerJoinEvent e) {
        if(!e.getPlayer().hasPlayedBefore()) {
            broadcast(String.valueOf(firstJoinMessage).replace("%player%", e.getPlayer().getName()));
            rulesNotAccepted.add(e.getPlayer().getUniqueId());
        }

        e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
    }

    private void broadcast(final String message) {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF("ALL");
        Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        p.sendPluginMessage(BananaCore.getInstance(), "BungeeCord", out.toByteArray());
    }

    public boolean hasAcceptedRules(UUID uuid) {
        return !rulesNotAccepted.contains(uuid);
    }

    public void acceptRules(UUID uuid) {
        rulesNotAccepted.remove(uuid);
    }

    public FileLoader getConfig() {
        return config;
    }
}
