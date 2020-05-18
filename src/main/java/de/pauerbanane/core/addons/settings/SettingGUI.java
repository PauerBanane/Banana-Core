package de.pauerbanane.core.addons.settings;

import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.settings.data.Settings;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class SettingGUI implements InventoryProvider {

    @Override
    public void init(Player player, InventoryContents content) {
        CorePlayer cp = CorePlayer.get(player.getUniqueId());
        Settings settings = cp.getData(Settings.class);

        content.fillBorders(ClickableItem.empty(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name("").build()));

        int column;

        // Auto-Sit de-/aktivieren
        column = 1;
        content.set(1, column, ClickableItem.empty(new ItemBuilder(Material.QUARTZ_STAIRS).name("§eSitzen")
                .lore("§fMit dieser Einstellung kannst")
                .lore("§fdu die §eautomatische Sitzfunktion")
                .lore("§2aktivieren§f, bzw. §4deaktivieren§f.").build()));
        content.set(2, column, ClickableItem.of(new ItemBuilder(settings.autoSitEnabled() ? Material.GREEN_DYE : Material.RED_DYE)
                .name(settings.autoSitEnabled() ? "§fSitzen §4deaktivieren" : "§fSitzen §2aktivieren").build(), click -> {
            settings.setAutoSit(!settings.autoSitEnabled());
            reOpen(player, content);
        }));

        // Scoreboards de-/aktivieren
        column = 3;
        content.set(1, column, ClickableItem.empty(new ItemBuilder(Material.BIRCH_SIGN).name("§eScoreboards")
                .lore("§fMit dieser Einstellung kannst")
                .lore("§fdu die Anzeige des §eScoreboards")
                .lore("§fan der rechte Seite §2einblenden§f,")
                .lore("§fbzw. §4ausblenden§f.").build()));
        content.set(2, column, ClickableItem.of(new ItemBuilder(settings.scoreboardEnabled() ? Material.GREEN_DYE : Material.RED_DYE)
                .name(settings.scoreboardEnabled() ? "§fScoreboards §4deaktivieren" : "§fScoreboards §2aktivieren").build(), click -> {
            settings.setScoreboardEnabled(!settings.scoreboardEnabled());
            reOpen(player, content);
        }));

        // Phantome de-/aktivieren
        column = 5;
        content.set(1, column, ClickableItem.empty(new ItemBuilder(Material.PHANTOM_MEMBRANE).name("§ePhantome")
                .lore("§fMit dieser Einstellung kannst")
                .lore("§fdu die §ePhantome §ffür")
                .lore("§fdich §4deaktivieren§f,")
                .lore("§fbzw. §2aktivieren§f.").build()));
        content.set(2, column, ClickableItem.of(new ItemBuilder(settings.phantomSpawnEnabled() ? Material.GREEN_DYE : Material.RED_DYE)
                .name(settings.phantomSpawnEnabled() ? "§fPhantome §4deaktivieren" : "§fPhantome §2aktivieren").build(), click -> {
            settings.setPhantomSpawnEnabled(!settings.phantomSpawnEnabled());
            reOpen(player, content);
        }));
    }
}
