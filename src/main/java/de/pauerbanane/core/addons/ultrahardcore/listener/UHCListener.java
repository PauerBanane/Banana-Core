package de.pauerbanane.core.addons.ultrahardcore.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pauerbanane.core.addons.ultrahardcore.UltraHardcore;
import de.pauerbanane.core.addons.ultrahardcore.data.UHCData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class UHCListener implements Listener {

    private UltraHardcore addon;

    public UHCListener(UltraHardcore addon) {
        this.addon = addon;
        Bukkit.getMessenger().registerOutgoingPluginChannel(addon.getPlugin(), "BungeeCord");
    }

    @EventHandler
    public void handleHealthRegen(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) e.setCancelled(true);
    }

    @EventHandler
    public void handleGhastDrop(EntityDeathEvent e) {
        if(!(e.getEntityType() == EntityType.GHAST)) return;
        ItemStack item = null;
        for(ItemStack drop : e.getDrops()) {
            if(drop.getType() == Material.GHAST_TEAR)
                item = drop;
        }

        if(item != null) {
            ItemStack ingot = new ItemStack(Material.GOLD_INGOT);
            ingot.setAmount(item.getAmount());
            e.getDrops().remove(item);
            e.getDrops().add(ingot);
        }
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        e.setDeathMessage("");
        sendChatMessage(player, "§e" + player.getName() + " §7ist im §aUltra-Hardcore-Event §7gestorben.");

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "advancement revoke " + player.getName() + " everything");
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "co rollback user:PauerBanane time:10000w");

        player.sendMessage("Du bist §cgestorben§f.\nDeine §eFortschritte §f werden zurückgesetzt...");

        UHCData data = CorePlayer.get(e.getEntity().getUniqueId()).getData(UHCData.class);
        if(data.getLifes() <= 1) {
            data.setLifes(0);
            player.kickPlayer("§fDu bist aus dem §eUltra-Hardcore-Event §fausgeschieden.\n§fVielen Dank für deine Teilname! §a:)");
        } else {
            data.setLifes(data.getLifes() - 1);
            player.sendMessage("§eDu besitzt noch §a" + data.getLifes() + " §eLeben.");
        }
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent e) {
        e.setRespawnLocation(Bukkit.getWorld("world").getSpawnLocation());
    }

    private void sendChatMessage(Player player, String message) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Message");
        out.writeUTF("ALL");
        out.writeUTF(message);

        player.sendPluginMessage(addon.getPlugin(), "BungeeCord", out.toByteArray());
    }

}
