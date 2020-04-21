package de.pauerbanane.core.addons.chestlock.commands;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.chestlock.ChestLock;
import de.pauerbanane.core.addons.chestlock.Lock;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import java.util.UUID;

@CommandAlias("lock")
public class ChestLockCommand extends BaseCommand {

    private final ChestLock addon;

    public ChestLockCommand(final ChestLock addon) {
        this.addon = addon;
    }

    private boolean canModifyArea(final Player player, final Block block) {
        if (player.hasPermission("worldguard.region.bypass." + block.getWorld().getName())) return true;

        final RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
        final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(player);

        return query.testBuild(BukkitAdapter.adapt(block.getLocation()), lp, Flags.BLOCK_PLACE);
    }

    @Default
    @Description("Verschließt einen Behälter.")
    public void lockCommand(final Player sender) {
        final RayTraceResult rt = sender.rayTraceBlocks(10);
        if (rt == null || rt.getHitBlock() == null) {
            sender.sendMessage(F.error("Lock", "Du musst einen Behälter anschauen."));
            return;
        }

        final Block block = rt.getHitBlock();

        if (!canModifyArea(sender, block)) {
            sender.sendMessage(F.error("Lock", "Du benötigst Baurechte in diesem Gebiet, um diese Kiste zu sichern."));
            return;
        }

        final BlockState state = block.getState();

        if (!(state instanceof TileState)) {
            sender.sendMessage(F.error("Lock", "§e" + state.getType().toString() + " §cist kein Behälter!"));
            return;
        }

        if (addon.isLocked(state) && !addon.canAccess(sender, (TileState) state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter gehört dir nicht."));
            return;
        }

        if (addon.lockChest(block, sender.getUniqueId())) {
            sender.sendMessage(F.main("Lock", "Der Behälter ist nun verschlossen."));
        } else {
            sender.sendMessage(F.error("Lock", "Der Behälter lässt sich nicht verschließen."));
        }
    }

    @Subcommand("addmember")
    public void addMember(final Player sender, @de.pauerbanane.acf.annotation.Flags("distance=5") final Block block, final OfflinePlayer target) {
        final BlockState state = block.getState();

        if (!addon.isLocked(state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter ist nicht verschlossen."));
            return;
        }

        if (!addon.canAccess(sender, (TileState) state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter gehört dir nicht."));
            return;
        }

        final TileState container = (TileState) state;

        if (addon.getLock(container).isPresent()) {

            if (addon.addMember(container, target)) {
                sender.sendMessage(F.main("Lock", "§e" + target.getName() + " §7hat nun Zugriff auf diesen Behälter."));
            } else {
                sender.sendMessage(F.error("Lock", "Es können maximal 5 andere Spieler Zugriff auf diese Kiste erhalten."));
                return;
            }
        } else {
            sender.sendMessage(F.error("Lock", "Es können maximal 5 andere Spieler Zugriff auf diese Kiste erhalten."));
        }
    }

    @Subcommand("delmember")
    @Syntax("<Spieler>")
    public void removeMember(final Player sender, @de.pauerbanane.acf.annotation.Flags("distance=5") final Block block, final OfflinePlayer target) {
        final BlockState state = block.getState();

        if (!addon.isLocked(state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter ist nicht verschlossen."));
            return;
        }

        if (!addon.canAccess(sender, (TileState) state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter gehört dir nicht."));
            return;
        }

        final TileState container = (TileState) state;

        if (addon.getLock(container).isPresent()) {
            addon.removeMember(container, target);
        } else {
            sender.sendMessage(F.error("Lock", "Dieser Behälter konnte nicht aktualisiert werden."));
        }
    }

    @Subcommand("info")
    @Description("Zeigt dir den Besitzer des Behälters an.")
    @Syntax("info")
    public void lockInfo(final Player sender, @de.pauerbanane.acf.annotation.Flags("distance=5") final Block block) {
        if (!(block.getState() instanceof TileState)) {
            sender.sendMessage(F.error("Lock", "Dieser Block lässt sich nicht verschließen."));
            return;
        }

        final TileState state = (TileState) block.getState();

        if(addon.isLocked(state)) {
            sender.sendMessage(F.error("Lock", "Dieser Behälter ist nicht gesichert."));
            return;
        }

        if (addon.getLock(state).isPresent()) {
            final Lock lock = addon.getLock(state).get();

            sender.sendMessage(F.main("Lock", "Diese Kiste gehört §e" + Bukkit.getOfflinePlayer(lock.getOwner()).getName() + "§7."));
        } else {
            sender.sendMessage("Dieser Behälter ist nicht verschlossen");
        }
    }

    @Subcommand("adminlock")
    @CommandPermission("command.adminlock")
    @Description("Setzt den angegebenen Spieler als Besitzer.")
    public void adminLock(final Player sender, final OfflinePlayer target) {
        if (!target.hasPlayedBefore()) {
            sender.sendMessage("Dieser Spieler existiert nicht.");
        }

        final UUID uuid = target.getUniqueId();

        final RayTraceResult rt = sender.rayTraceBlocks(10);
        if (rt == null || rt.getHitBlock() == null) {
            sender.sendMessage(F.error("Lock", "Du musst einen Behälter anschauen."));
            return;
        }

        final Block block = rt.getHitBlock();

        if (addon.lockChest(block, uuid)) {
            sender.sendMessage(F.main("Lock", "Der Behälter ist nun verschlossen."));
        } else {
            sender.sendMessage(F.error("Lock", "Der Behälter lässt sich nicht verschließen."));
        }
    }
}