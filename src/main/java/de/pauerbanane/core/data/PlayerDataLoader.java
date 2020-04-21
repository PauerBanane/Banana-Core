package de.pauerbanane.core.data;

import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerDataLoader implements Listener {

    private BananaCore plugin;

    public PlayerDataLoader(BananaCore plugin) {
        this.plugin = plugin;

        plugin.registerListener(this);
    }

    /*  LoadOnlinePlayers - Lädt beim Start alle Spieler die Online sind
     *
     *  PlayerDataManager muss zusätzlich geladen werden,
     *  da dieser nur Join und QuitEvent abfragt.
     */
    public void loadOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            plugin.getPlayerDataManager().loadData(player.getUniqueId());
            new CorePlayer(player.getUniqueId());
        });
    }

    /*  JoinEvent - Lädt beim Join einen neuen CorePlayer
     *
     *  Muss im PlayerDataManager nicht geladen werden,
     *  da dieser das JoinEvent selber abfragt.
     */
    @EventHandler
    public void loadPlayer(PlayerJoinEvent e) {
        if(PermissionManager.hasMetaValue(e.getPlayer(), "homelimit", 2, null, null)) {
            String value = PermissionManager.getMetaValue(e.getPlayer().getUniqueId(), "homelimit", null, null);
            if(!UtilMath.isInt(value)) return;
            MetaNode newNode = PermissionManager.buildMetaNode("homelimit", value, "survival", null);
            MetaNode oldNode = PermissionManager.buildMetaNode("homelimit", value, null, null);

            PermissionManager.removeNode(e.getPlayer(), oldNode);
            PermissionManager.addNode(e.getPlayer(), newNode);

            BananaCore.getInstance().getLogger().info("Reassigned MetaNode 'homelimit' for Player " + e.getPlayer().getName() + " with value '" + value + "'");
        }
        if(PermissionManager.hasNode(e.getPlayer().getUniqueId(), PermissionManager.buildPermissionNode("lizenz.endwelt", true, null, null))) {
            PermissionNode oldNode = PermissionManager.buildPermissionNode("lizenz.endwelt", true, null, null);
            PermissionNode newNode = PermissionManager.buildPermissionNode("lizenz.endwelt", true, "survival", null);

            PermissionManager.removeNode(e.getPlayer(), oldNode);
            PermissionManager.addNode(e.getPlayer(), newNode);

            BananaCore.getInstance().getLogger().info("Reassigned PermissionNode 'lizenz.endwelt' for Player " + e.getPlayer().getName());
        }
        if(PermissionManager.hasNode(e.getPlayer().getUniqueId(), PermissionManager.buildPermissionNode("kccore.plots.shops", true, null, null))) {
            PermissionNode oldNode = PermissionManager.buildPermissionNode("kccore.plots.shops", true, null, null);
            PermissionNode newNode = PermissionManager.buildPermissionNode("plots.shops", true, "survival", null);

            PermissionManager.removeNode(e.getPlayer(), oldNode);
            PermissionManager.addNode(e.getPlayer(), newNode);

            BananaCore.getInstance().getLogger().info("Reassigned PermissionNode 'plots.shops' for Player " + e.getPlayer().getName());
        }
        new CorePlayer(e.getPlayer().getUniqueId());
    }

    /*  QuitEvent - Löscht beim Quit einen bestehenden CorePlayer
     *
     *  Muss im PlayerDataManager nicht geladen werden
     *  da dieser das QuitEvent selber abfragt.
     */
    @EventHandler
    public void unloadPlayer(PlayerQuitEvent e) {
        CorePlayer.get(e.getPlayer().getUniqueId()).unload();
    }
}
