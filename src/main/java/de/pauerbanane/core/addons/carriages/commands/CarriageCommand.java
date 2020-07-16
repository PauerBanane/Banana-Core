package de.pauerbanane.core.addons.carriages.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.carriages.Carriage;
import de.pauerbanane.core.addons.carriages.CarriageLine;
import de.pauerbanane.core.addons.carriages.CarriageManager;
import de.pauerbanane.core.addons.carriages.Carriages;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("carriage")
@CommandPermission("command.carriage")
public class CarriageCommand extends BaseCommand {

    private Carriages addon;

    private CarriageManager manager;

    public CarriageCommand(Carriages addon) {
        this.addon = addon;
        this.manager = addon.getManager();
    }

    @Subcommand("createline")
    public void createLine(Player sender, String name) {
        if(manager.getCarriageLine(name) != null) {
            sender.sendMessage(F.error("Carriage", "Diese Reiselinie existiert bereits."));
            return;
        }

        manager.createNewLine(name, sender.getWorld());
        sender.sendMessage(F.main("Carriage", "Du hast die Reiselinie §e" + name + " §7erstellt."));
    }

    @Subcommand("removeline")
    @CommandCompletion("@carriageline")
    public void removeLine(Player sender, CarriageLine carriageLine) {
        manager.removeLine(carriageLine);
        sender.sendMessage(F.main("Carriage", "Du hast die Reiselinie §e" + carriageLine.getName() + " §7entfernt."));
    }

    @Subcommand("addcarriage")
    @CommandCompletion("@carriageline @nothing @region @material")
    public void addCarriage(Player sender, CarriageLine carriageLine, String name, String regionID, Material material) {
        if(carriageLine.hasCarriage(name)) {
            sender.sendMessage(F.error("Carriage", "Dieses Reiseziel existiert in der Linie bereits."));
            return;
        }
        if(!carriageLine.getWorld().equalsIgnoreCase(sender.getWorld().getName())) {
            sender.sendMessage(F.error("Carriage", "Das Reiseziel darf nicht in einer anderen Welt liegen."));
            return;
        }
        if(manager.isCarriageRegion(regionID, sender.getWorld().getName())) {
            sender.sendMessage(F.error("Carriage", "Diese Region ist bereits von einem anderen Reiseziel belegt."));
            return;
        }

        Carriage carriage = new Carriage(name, regionID, sender.getLocation(), material);
        carriageLine.addCarriage(carriage);
        sender.sendMessage(F.main("Carriage", "Du hast ein neues Reiseziel zur Linie §e" + carriageLine.getName() + " §7hinzugefügt."));
    }

    @Subcommand("removecarriage")
    @CommandCompletion("@carriageline")
    public void removeCarriage(Player sender, CarriageLine carriageLine, String carriageName) {
        if(!carriageLine.hasCarriage(carriageName)) {
            sender.sendMessage(F.error("Carriage", "Dieses Reiseziel existiert in der Linie nicht."));
            return;
        }

        Carriage carriage = carriageLine.getCarriage(carriageName);
        carriageLine.removeCarriage(carriage);
        sender.sendMessage(F.main("Carriage", "Du hast das Reiseziel aus der Linie entfernt."));
    }

    @Subcommand("list")
    @CommandCompletion("@carriageline")
    public void listCarriages(Player sender, CarriageLine carriageLine) {
        sender.sendMessage(F.main("Carriage", "§e" + carriageLine.getName() + "§7: " + carriageLine.getCarriageNames()));
    }

}
