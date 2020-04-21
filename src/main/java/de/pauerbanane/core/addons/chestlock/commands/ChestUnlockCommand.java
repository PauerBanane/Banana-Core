package de.pauerbanane.core.addons.chestlock.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Description;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.chestlock.ChestLock;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

@CommandAlias("unlock")
public class ChestUnlockCommand extends BaseCommand {

    private final ChestLock addon;

    public ChestUnlockCommand(final ChestLock addon) {
        this.addon = addon;
    }

    @Default
    @Description("Öffnet einen Behälter.")
    public void unlockCommand(final Player sender) {
        final RayTraceResult rt = sender.rayTraceBlocks(10);
        if (rt == null || rt.getHitBlock() == null) {
            sender.sendMessage(F.error("Lock", "Du musst einen Behälter anschauen."));
            return;
        }

        final Block block = rt.getHitBlock();
        final BlockState state = block.getState();

        if (!addon.isLocked(state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter ist nicht verschlossen."));
            return;
        }

        if (!(state instanceof TileState)) {
            sender.sendMessage(F.error("Lock", "§e" + state.getType().toString() + " §cist kein Behälter!"));
            return;
        }

        if (!addon.canAccess(sender, (TileState) state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter gehört dir nicht."));
            return;
        }

        addon.unlockChest(block);
        sender.sendMessage(F.main("Lock", "Der Behälter wurde §2aufgeschlossen§7."));
    }
}