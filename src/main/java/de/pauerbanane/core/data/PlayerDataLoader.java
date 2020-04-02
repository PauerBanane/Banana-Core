package de.pauerbanane.core.data;

import de.pauerbanane.core.BananaCore;
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
