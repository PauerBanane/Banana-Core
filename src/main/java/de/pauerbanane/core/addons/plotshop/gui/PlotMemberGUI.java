package de.pauerbanane.core.addons.plotshop.gui;

import com.sk89q.worldguard.domains.DefaultDomain;
import de.pauerbanane.api.anvilgui.AnvilGUI;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.smartInventory.content.SlotPos;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.api.util.UtilItem;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlotMemberGUI implements InventoryProvider {
    private final Plot plot;

    private ItemStack border = (new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE)).name(" ").build();

    public PlotMemberGUI(Plot plot) {
        this.plot = plot;
    }

    public void init(Player player, InventoryContents contents) {
        contents.fillBorders(ClickableItem.empty(this.border));
        for (UUID uuid : this.plot.getRegion().getMembers().getUniqueIds()) {
            ItemStack skull = new ItemBuilder(UtilItem.getPlayerHead(uuid)).name(Bukkit.getOfflinePlayer(uuid).getName()).lore("§fKlicke um den Spieler zu entfernen.").build();
            contents.add(ClickableItem.of(skull, e -> {
                this.plot.getRegion().getMembers().removePlayer(uuid);
                player.sendMessage(F.main("Plot", "Du hast die Baurechte von §e" + Bukkit.getOfflinePlayer(uuid).getName() + " §7entfernt."));
                player.closeInventory();
            }));
        }
        contents.set(SlotPos.of(3, 4), ClickableItem.of(new ItemBuilder(Material.EMERALD).name("§2Spieler hinzufügen").build(),  e -> {
            new AnvilGUI.Builder().item(new ItemBuilder(Material.PLAYER_HEAD).name("§2Spieler hinzufügen").build())
                                  .onComplete((p,t) -> {
                                      if(!BananaCore.getInstance().getPlayerDataManager().getPluginStorage(BananaCore.getInstance()).checkIfExists(t))
                                          return AnvilGUI.Response.text("§cSpieler existiert nicht");
                                      UUID target = BananaCore.getInstance().getPlayerDataManager().getPluginStorage(BananaCore.getInstance()).getIDbyName(t);
                                      DefaultDomain members = this.plot.getRegion().getMembers();
                                      members.addPlayer(target);
                                      this.plot.getRegion().setMembers(members);
                                      player.sendMessage("Du hast §e" + t + " §7Baurechte auf diesem Grundstgegeben.");
                                      UtilPlayer.playSound(player, Sound.BLOCK_NOTE_BLOCK_GUITAR, 0.8F, 1.25F);
                                      reOpen(player, contents);
                                      return AnvilGUI.Response.close();
                                  })
                                  .title("§2Spieler hinzufügen")
                                  .plugin(BananaCore.getInstance())
                                  .open(player);
        }));
    }
}