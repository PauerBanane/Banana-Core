package de.pauerbanane.core.addons.regrowingTrees.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandCompletion;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.regrowingTrees.RegrowingTrees;
import de.pauerbanane.core.addons.regrowingTrees.Tree;
import org.bukkit.entity.Player;

import java.io.IOException;

@CommandAlias("regrowingtree|rtree")
@CommandPermission("command.regrowingtree")
public class RegrowingTreeCommand extends BaseCommand {

    private RegrowingTrees addon;

    public RegrowingTreeCommand(RegrowingTrees addon) {
        this.addon = addon;
    }

    @Subcommand("create")
    @CommandCompletion("@region")
    public void create(Player sender, String regionID) {
        if(!WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sender.getWorld())).hasRegion(regionID)) {
            sender.sendMessage(F.error("Trees", "Es existiert keine Region mit dieser ID."));
            return;
        }
        if(addon.getManager().getTree(regionID) != null) {
            sender.sendMessage(F.error("Trees", "Auf dieser Region wächst bereits ein Baum!"));
            return;
        }

        ProtectedRegion region = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(sender.getWorld())).getRegion(regionID);
        Tree tree = new Tree(regionID, region, sender.getWorld());
        try {
            addon.getManager().registerTree(tree);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.sendMessage(F.main("Trees", "Auf der Region §e" + regionID + " §7wächst nun ein Baum."));
    }

    @Subcommand("remove")
    @CommandCompletion("@regrowingTree")
    public void delete(Player sender, Tree tree) {
        addon.getManager().deleteTree(tree);
        sender.sendMessage(F.main("Trees", "Du hast den Baum §e" + tree.getRegionID() + " §7gelöscht."));
    }

}
