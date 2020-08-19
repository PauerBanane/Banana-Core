package de.pauerbanane.core.addons.quests.objectives;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.api.regionevents.RegionEnterEvent;
import de.pauerbanane.core.util.UtilWorldGuard;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

public class RegionEnterObjective extends CustomObjective implements Listener {

    // Get the Quests plugin
    Quests qp = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");

    // Construct the objective
    public RegionEnterObjective() {
        this.setName("Region betreten");
        this.setAuthor("PauerBanane");
        this.setShowCount(true);
        this.addStringPrompt("Region", "Gebe die Region ein, die der Spieler betreten muss.", "NICHT KONFIGURIERT");
        this.setCountPrompt("Wie oft soll die Region betreten werden?");
        this.setDisplay("§7Betrete die Region: §e%Region%");
    }

    // Catch the Bukkit event for a player gaining/losing exp
    @EventHandler
    public void onPlayerExpChange(RegionEnterEvent e) {
        // Make sure to evaluate for all of the player's current quests
        for (Quest quest : qp.getQuester(e.getPlayer().getUniqueId()).getCurrentQuests().keySet()) {
            Map<String, Object> map = getDataForPlayer(e.getPlayer(), this, quest);
            String userInput = (String) map.get("Region");
            ProtectedRegion targetRegion = UtilWorldGuard.getRegion(userInput, e.getPlayer().getWorld());
            if (targetRegion == null) return;

            // Check if the item the player dropped is the one user specified
            if (targetRegion == e.getRegion()) {
                incrementObjective(e.getPlayer(), this, 1, quest);
            }
        }
    }

}
