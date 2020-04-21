package de.pauerbanane.core.addons.playershop;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.entity.Player;

@CommandAlias("shop")
@CommandPermission("command.shop")
public class ShopCommand extends BaseCommand {

    private final ShopManager manager;

    public ShopCommand(ShopManager manager) {
        this.manager = manager;
    }

    @Subcommand("create")
    @CommandCompletion("@plot")
    public void createShop(Player sender, Plot plot) {
        Shop shop = this.manager.createShop(sender.getLocation(), plot.getRegionID());
        sender.sendMessage("§2Shop für Region " + shop.getPlotRegion() + " wurde erstellt.");
    }

    @Subcommand("save")
    public void save(Player sender) {
        manager.saveAllShops();
        sender.sendMessage(F.main("Shop", "Alle Shops wurden gespeichert."));
    }

    @Subcommand("remove")
    @CommandCompletion("@shop")
    public void deleteShop(Player sender, Shop shop) {
        if (this.manager.deleteShop(shop.getPlotRegion())) {
            sender.sendMessage("§2Der angegebene Shop wurde gelöscht.");
        } else {
            sender.sendMessage("§cEs existiert kein Shop mit dieser ID");
        }
    }

    @Subcommand("info")
    @CommandCompletion("@shop")
    public void shopInfo(Player sender, Shop shop) {
        sender.sendMessage("Aktiv:" + shop.isActive());
        sender.sendMessage("Owner: " + shop.getOwner());
    }
}