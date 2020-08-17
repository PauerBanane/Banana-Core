package de.pauerbanane.core.addons.blockrefresh.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.blockrefresh.BlockRefreshAddon;
import de.pauerbanane.core.addons.blockrefresh.RefreshChain;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.sql.Ref;

@CommandAlias("blockrefresh")
@CommandPermission("command.blockrefresh")
public class BlockRefreshCommand extends BaseCommand {

    private BlockRefreshAddon addon;

    public BlockRefreshCommand(BlockRefreshAddon addon) {
        this.addon = addon;
    }

    @Subcommand("list")
    public void list(Player sender) {
        sender.sendMessage(F.main("BlockRefresh", "RefreshChains: §e" + addon.getChains().keySet().toArray()));
    }

    @Subcommand("create")
    @CommandCompletion("@material")
    public void create(Player sender, Material origin) {
        if (addon.registerChain(new RefreshChain(true, origin))) {
            sender.sendMessage(F.main("BlockRefresh", "Die RefreshChain wurde erstellt."));
        } else
            sender.sendMessage(F.error("BlockRefresh", "Diese RefreshChain existiert bereits."));
    }

    @Subcommand("enable")
    @CommandCompletion("@refreshchain")
    public void enable(Player sender, RefreshChain chain) {
        if (!chain.isEnabled()) {
            chain.setEnabled(true);
            sender.sendMessage(F.main("BlockRefresh", "Die RefreshChain wurde §2aktiviert§7."));
        } else
            sender.sendMessage(F.error("BlockRefresh", "Die RefreshChain ist bereits §2aktiviert§7."));
    }

    @Subcommand("disable")
    @CommandCompletion("@refreshchain")
    public void disable(Player sender, RefreshChain chain) {
        if (chain.isEnabled()) {
            chain.setEnabled(false);
            sender.sendMessage(F.main("BlockRefresh", "Die RefreshChain wurde §4deaktiviert§7."));
            addon.forceFinishAndRemove(chain);
        } else
            sender.sendMessage(F.error("BlockRefresh", "Die RefreshChain ist bereits §4deaktiviert§7."));
    }

    @Subcommand("remove")
    @CommandCompletion("@refreshchain")
    public void remove(Player sender, RefreshChain chain) {
        addon.unregisterChain(chain);
        sender.sendMessage(F.main("BlockRefresh", "Die RefreshChain wurde gelöscht."));
    }

    @Subcommand("addmaterial")
    @CommandCompletion("@refreshchain @material")
    public void addMaterial(Player sender, RefreshChain chain, Material material) {
        if (chain.addMaterial(material)) {
            sender.sendMessage(F.main("BlockRefresh", "§e" + material.toString() + " §7wurde hinzugefügt."));
        } else
            sender.sendMessage(F.error("BlockRefresh", "§e" + material.toString() + " §7wurde bereits hinzugefügt."));
    }

    @Subcommand("removematerial")
    @CommandCompletion("@refreshchain @material")
    public void removeMaterial(Player sender, RefreshChain chain, Material material) {
        if (chain.removeMaterial(material)) {
            sender.sendMessage(F.main("BlockRefresh", "§e" + material.toString() + " §7wurde entfernt."));
        } else
            sender.sendMessage(F.error("BlockRefresh", "§e" + material.toString() + " §7kann nicht entfernt werden."));
    }

}
