package de.pauerbanane.core.addons.lobby.server;

import com.google.common.collect.Lists;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.lobby.Lobby;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ServerInterfaceManager implements Listener {

    private Lobby addon;

    private FileLoader config;

    private ArrayList<Server> servers;

    private String serverInterfaceItemName;

    private ItemStack serverInterfaceItem;

    public ServerInterfaceManager(Lobby addon) {
        this.addon = addon;
        this.config = new FileLoader(addon.getAddonFolder(), "Lobby.yml");
        this.servers = Lists.newArrayList();
        this.serverInterfaceItemName = "§7>§6> §e§lServer auswählen";
        this.serverInterfaceItem = new ItemBuilder(Material.COMPASS).name(serverInterfaceItemName).build();

        loadConfig();
    }

    private void loadConfig() {
        if(!config.isSet("servers")) {
            config.set("servers.1.displayName", "§6Survival");
            config.set("servers.1.targetServer", "survival");
            config.set("servers.1.icon", "DIAMOND");
            List<String> lore = Lists.newArrayList("§2Online");
            config.set("servers.1.lore", lore);
            config.set("servers.1.permission", "server.survival");
            config.set("servers.1.showWithoutPermission", true);

            config.save();
        }

        ConfigurationSection section = config.getConfigurationSection("servers");
        for(String entry : section.getKeys(false)) {
            ConfigurationSection serverSection = section.getConfigurationSection(entry);
            String displayName = serverSection.getString("displayName");
            String targetServer = serverSection.getString("targetServer");
            Material icon = Material.getMaterial(serverSection.getString("icon", "PAPER"));
            List<String> lore = serverSection.getStringList("lore");
            String permission = serverSection.getString("permission");
            Boolean showWithoutPermission = serverSection.getBoolean("showWithoutPermission");

            Server server = new Server(displayName, targetServer, icon, lore, permission, showWithoutPermission);
            servers.add(server);
        }
    }

    private int getVisibleServers(Player player) {
        int amount = (int) servers.stream().filter(server -> server.isVisible(player)).count();
        return amount;
    }

    @EventHandler
    public void handleServerInterfaceOpen(PlayerInteractEvent e) {
        if (!addon.hasAcceptedRules(e.getPlayer().getUniqueId())) {
            e.getPlayer().sendMessage(F.error("Regeln", "Du musst zuerst die Regeln akzeptieren. §e/accept"));
            e.setCancelled(true);
            return;
        }
        Player p = e.getPlayer();
        ItemStack itemInHand = p.getInventory().getItemInMainHand();

        if(itemInHand.getType() == Material.AIR) return;
        if(!itemInHand.getItemMeta().getDisplayName().equalsIgnoreCase(serverInterfaceItemName)) return;

        int size = ((int) (getVisibleServers(p) - 1) / 3) + 3;

        SmartInventory.builder().provider(new ServerGUI(this)).title("§7Server-Auswahl").size(size).build().open(p);
    }

    public ArrayList<Server> getServers() {
        return servers;
    }

    public Lobby getAddon() {
        return addon;
    }

    public ItemStack getServerInterfaceItem() {
        return serverInterfaceItem;
    }
}
