package de.pauerbanane.core.addons.chestlock;

import com.destroystokyo.paper.MaterialSetTag;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.chestlock.commands.ChestLockCommand;
import de.pauerbanane.core.addons.chestlock.commands.ChestUnlockCommand;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.RayTraceResult;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ChestLock extends Addon implements Listener {

    private MaterialSetTag chests;

    private NamespacedKey lockID;

    private final Set<BlockFace> facings = Sets.newHashSetWithExpectedSize(4);

    private final com.google.gson.Gson mapper = new Gson();

    private final HashMap<UUID, Cache<Long, Lock>> cache = Maps.newHashMap();

    @Override
    public void onEnable() {
        lockID = new NamespacedKey(getPlugin(), "jsonLock");

        chests = new MaterialSetTag(new NamespacedKey(getPlugin(), "protectedChests"),
                Material.CHEST,
                Material.TRAPPED_CHEST,
                Material.FURNACE,
                Material.BLAST_FURNACE,
                Material.SMOKER,
                Material.BARREL,
                Material.SHULKER_BOX
        );

        facings.add(BlockFace.EAST);
        facings.add(BlockFace.NORTH);
        facings.add(BlockFace.SOUTH);
        facings.add(BlockFace.WEST);

        commandManager.getCommandContexts().registerIssuerAwareContext(Block.class, issuer -> {
            final Player player = issuer.getPlayer();

            final double distance = issuer.getFlagValue("distance", 10);

            final RayTraceResult result = player.rayTraceBlocks(distance, FluidCollisionMode.NEVER);

            if (result == null || result.getHitBlock() == null) {
                throw new InvalidCommandArgument("Du musst einen Block anschauen.");
            }

            return result.getHitBlock();
        });

        registerCommand(new ChestLockCommand(this));
        registerCommand(new ChestUnlockCommand(this));

        Bukkit.getPluginManager().registerEvents(this, getPlugin());

        //Fill Cache Map with all world IDs
        for (final World world : Bukkit.getWorlds()) {
            cache.put(world.getUID(), CacheBuilder.newBuilder().maximumSize(300).expireAfterAccess(10, TimeUnit.MINUTES).build());
        }

        //Clean Cache every 30 Minutes
        Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> cache.values().forEach(Cache::cleanUp), 36000, 36000);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        cache.put(event.getWorld().getUID(), CacheBuilder.newBuilder().maximumSize(500).expireAfterAccess(10, TimeUnit.MINUTES).build());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChestPlace(final BlockPlaceEvent event) {
        if (event.isCancelled() || !chests.isTagged(event.getBlock())) {
            return;
        }

        final Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        final Block block = event.getBlock();
        boolean canLock = true;

        for (final BlockFace face : facings) {
            final Block relative = block.getRelative(face);
            if (!chests.isTagged(relative)) {
                continue;
            }

            final TileState chest = (TileState) relative.getState();

            if (isLocked(chest) && !canAccess(player, chest)) {
                event.setCancelled(true);
                player.sendMessage("Du kannst hier keine Kiste platzieren.");
                canLock = false;
                break;
            }
        }

        if (canLock && !player.isSneaking()) {
            lockChest(block, player.getUniqueId());
            player.sendMessage(F.main("Lock", "Der Behälter ist nun verschlossen."));
        }
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {

        for(Block block : e.getBlocks()) {

            if(!chests.isTagged(block))
                continue;

            if (!(block.getState() instanceof TileState))
                return;

            TileState tileState = (TileState) block.getState();

            if (isLocked(tileState)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent e) {

        for(Block block : e.getBlocks()) {

            if(!chests.isTagged(block))
                continue;

            if (!(block.getState() instanceof TileState))
                return;

            TileState tileState = (TileState) block.getState();

            if (isLocked(tileState)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    @EventHandler
    public void onExplosion(BlockExplodeEvent e) {
        if (!chests.isTagged(e.getBlock()))
            return;

        if (!(e.getBlock().getState() instanceof TileState))
            return;

        TileState tileState = (TileState) e.getBlock().getState();

        if (isLocked(tileState))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChestInteract(final PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (!chests.isTagged(event.getClickedBlock())) {
            return;
        }

        final Player player = event.getPlayer();
        final BlockState state = event.getClickedBlock().getState();

        if (!(state instanceof TileState)) {
            return;
        }

        if (!isLocked(state)) {
            return;
        }

        if (isLocked(state) && canAccess(player, (TileState) state)) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                final OfflinePlayer op = Bukkit.getOfflinePlayer(getLock((TileState) state).get().getOwner());
                player.sendMessage(F.main("Lock", "Der Behälter wurde von §e" + op.getName() + " §7verschlossen."));
                return;
            }
            return;
        }

        event.setCancelled(true);
        player.sendActionBar("§cDieser Behälter ist verschlossen");
        UtilPlayer.playSound(player, Sound.BLOCK_CHEST_LOCKED);
    }

    @EventHandler
    public void HopperMoveItem(final InventoryMoveItemEvent event) {
        if (event.getSource().getType() != InventoryType.CHEST || event.getSource().getLocation() == null) {
            return;
        }

        final Block block = event.getSource().getLocation().getBlock();
        if (!chests.isTagged(block)) {
            return;
        }

        if (isLocked(block.getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChestBreak(final BlockBreakEvent event) {
        if (!chests.isTagged(event.getBlock())) {
            return;
        }

        final Player player = event.getPlayer();
        final BlockState state = event.getBlock().getState();

        if (!(state instanceof TileState)) {
            return;
        }

        if (isLocked(state)) {

            if (!canAccess(player, (TileState) state)) {
                event.setCancelled(true);
                player.sendActionBar("§cDiese Kiste ist gesichert und kann nicht zerstört werden.");
            } else {
                cache.get(state.getWorld().getUID()).invalidate(state.getBlock().getBlockKey());
            }
        }
    }

    public boolean canAccess(final Player player, final TileState container) {
        if (player.hasPermission("lock.bypass")) {
            return true;
        }

        return getLock(container).get().canAccess(player.getUniqueId());
    }

    public boolean isLocked(final BlockState state) {
        if (!(state instanceof TileState)) {
            return false;
        }

        final TileState chest = (TileState) state;
        return chest.getPersistentDataContainer().has(lockID, PersistentDataType.STRING);
    }

    public Optional<Lock> getLock(final TileState state) {
        Lock lock = null;
        try {
            lock = cache.get(state.getWorld().getUID()).get(state.getBlock().getBlockKey(), () -> {
                final String content = state.getPersistentDataContainer().get(lockID, PersistentDataType.STRING);
                return mapper.fromJson(content, Lock.class);
            });
        } catch (final ExecutionException ex) {
            ex.printStackTrace();
        }
        return Optional.ofNullable(lock);
    }

    public boolean addMember(final TileState container, final OfflinePlayer target) {
        final Optional<Lock> tlock = getLock(container);

        if (tlock.isPresent()) {
            final Lock lock = tlock.get();
            lock.addMember(target.getUniqueId());

            return updateLock(container, lock);
        } else {
            return false;
        }

    }

    public boolean removeMember(final TileState container, final OfflinePlayer target) {
        final Optional<Lock> tlock = getLock(container);

        if (tlock.isPresent()) {
            final Lock lock = tlock.get();
            lock.removeMember(target.getUniqueId());
            return updateLock(container, lock);
        } else {
            return false;
        }

    }

    public boolean lockChest(final Block block, final UUID uuid) {
        final BlockState state = block.getState();

        if (!(state instanceof TileState)) {
            return false;
        }

        final TileState container = (TileState) state;

        final Lock lock = new Lock(uuid);

        cache.get(state.getWorld().getUID()).put(block.getBlockKey(), lock);

        return updateLock(container, lock);
    }

    public void unlockChest(final Block block) {
        final BlockState state = block.getState();

        if (!(state instanceof TileState)) {
            return;
        }
        final TileState container = (TileState) state;
        getLock(container).ifPresent(lock -> updateLock(container, null));
    }


    private boolean updateLock(final TileState state, @Nullable final Lock lock) {
        try {
            if (state instanceof Chest && ((Chest) state).getInventory().getHolder() instanceof DoubleChest) {
                final Chest chest = (Chest) state;
                final InventoryHolder holder = chest.getInventory().getHolder();

                if (holder instanceof DoubleChest) {
                    final DoubleChest dc = (DoubleChest) holder;

                    final TileState left = (TileState) dc.getLeftSide();
                    final TileState right = (TileState) dc.getRightSide();

                    if (lock == null) {

                        left.getPersistentDataContainer().remove(lockID);
                        right.getPersistentDataContainer().remove(lockID);

                        cache.get(state.getWorld().getUID()).invalidate(left.getBlock().getBlockKey());
                        cache.get(state.getWorld().getUID()).invalidate(right.getBlock().getBlockKey());

                    } else {
                        left.getPersistentDataContainer().set(lockID, PersistentDataType.STRING, mapper.toJson(lock));
                        right.getPersistentDataContainer().set(lockID, PersistentDataType.STRING, mapper.toJson(lock));
                    }

                    left.update(true);
                    right.update(true);
                    return true;
                }
            }

            if (lock == null) {
                state.getPersistentDataContainer().remove(lockID);
                cache.get(state.getWorld().getUID()).invalidate(state.getBlock().getBlockKey());
            } else {
                state.getPersistentDataContainer().set(lockID, PersistentDataType.STRING, mapper.toJson(lock));
            }
            state.update(true);
            return true;
        } catch (final Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }
}