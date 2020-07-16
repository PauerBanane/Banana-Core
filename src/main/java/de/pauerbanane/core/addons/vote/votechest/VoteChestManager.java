package de.pauerbanane.core.addons.vote.votechest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.FileLoader;
import de.pauerbanane.api.util.UtilLoc;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.carriages.Carriage;
import de.pauerbanane.core.addons.chairs.events.SitEvent;
import de.pauerbanane.core.addons.vote.VoteAddon;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.commands.VoteChestCommand;
import de.pauerbanane.core.addons.vote.votechest.gui.VoteChestGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class VoteChestManager implements Listener {

    private VoteAddon addon;

    private ArrayList<VoteChest> voteChests;

    private FileLoader config;

    private boolean isVoteEventRunning = false;

    private ArrayList<VoteKey> voteKeys;

    public VoteChestManager(VoteAddon addon) {
        this.addon = addon;
        this.voteChests = Lists.newArrayList();
        this.voteKeys = Lists.newArrayList();
        ConfigurationSerialization.registerClass(VoteKey.class, "voteKey");
        this.config = new FileLoader(addon.getAddonFolder(), "VoteChest.yml");

        load();

        commandSetup();

        BananaCore.getInstance().getPlayerDataManager().registerPlayerData(BananaCore.getInstance(), VoteData.class);

        addon.registerCommand(new VoteChestCommand(this));

        addon.registerListener(this);
    }

    @EventHandler
    public void handleChestOpen(PlayerInteractEvent e) {
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.CHEST) return;
        if (getOccupiedVoteChest(e.getPlayer()) == null) return;
        VoteChest voteChest = getOccupiedVoteChest(e.getPlayer());

        if(!voteChest.isChest(e.getClickedBlock())) return;
        voteChest.openChest(e.getClickedBlock());
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if(!isVoteChest(e.getClickedBlock())) return;
        e.setCancelled(true);

        VoteChest voteChest = getVoteChest(e.getClickedBlock());
        if(voteChest.isOccupied()) {
            e.getPlayer().sendMessage(F.error("Vote", "Diese VoteChest wird gerade benutzt."));
            return;
        }

        UtilPlayer.playSound(e.getPlayer(), Sound.BLOCK_CHEST_OPEN);
        SmartInventory.builder().provider(new VoteChestGUI(this, voteChest)).title("§e§lVote-Chest").size(3).build().open(e.getPlayer());
    }

    @EventHandler
    public void handleDisconnect(PlayerQuitEvent e) {
        if(!isOpeningVoteChest(e.getPlayer())) return;
        getOccupiedVoteChest(e.getPlayer()).stopEvent();
    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        if(isOpeningVoteChest(e.getPlayer()) && e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) e.setCancelled(true);
    }

    @EventHandler
    public void handleSit(SitEvent e) {
        if(isOpeningVoteChest(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void handleMovement(PlayerMoveEvent e) {
        if (!isOpeningVoteChest(e.getPlayer())) return;

        if(e.getFrom().getX() != e.getTo().getX() ||
           e.getFrom().getZ() != e.getTo().getZ())
            e.setCancelled(true);
    }

    public boolean isOpeningVoteChest(Player player) {
        for(VoteChest voteChest : voteChests) {
            if (voteChest.isOccupied() && voteChest.getOccupier() == player.getUniqueId()) return true;
        }

        return false;
    }

    public void startVoteChestEvent(Player player, VoteChest voteChest, VoteKey.Type voteKey) {
        voteChest.startEvent(player,getVoteKey(voteKey));
    }

    public void addVoteChest(Block block) {
        voteChests.add(new VoteChest(block));
    }

    public void removeVoteChest(Block block) {
        voteChests.remove(getVoteChest(block));
    }

    private void load() {
        if(config.isSet("voteChests")) {
            List<String> serializedLocations = config.getStringList("voteChests");
            serializedLocations.forEach(serializedLocation -> {
                if (UtilLoc.deserialize(serializedLocation) != null) {
                    Block block = UtilLoc.deserialize(serializedLocation).getBlock();
                    if(block.getType() == Material.ENDER_CHEST) {
                        voteChests.add(new VoteChest(block));
                    } else
                        addon.getPlugin().getLogger().warning("Failed to load VoteChest at " + serializedLocation + ". Deleting...");
                } else
                    addon.getPlugin().getLogger().warning("Failed to load VoteChest at " + serializedLocation + ". Deleting...");
            });

            addon.getPlugin().getLogger().info("Loaded " + voteChests.size() + " Vote-Chests");
        }

        if(config.isSet("commonItems") && config.isSet("rareItems") && config.isSet("veryRareItems")) {
            voteKeys.add(config.getConfig().getSerializable(VoteKey.Type.OLD_KEY.toString(), VoteKey.class));
            voteKeys.add(config.getConfig().getSerializable(VoteKey.Type.ANCIENT_KEY.toString(), VoteKey.class));
            voteKeys.add(config.getConfig().getSerializable(VoteKey.Type.EPIC_KEY.toString(), VoteKey.class));
        } else {
            voteKeys.add(new VoteKey(VoteKey.Type.OLD_KEY, Lists.newArrayList(new ItemStack(Material.WHEAT)), Lists.newArrayList(new ItemStack(Material.GOLD_INGOT)), Lists.newArrayList(new ItemStack(Material.DIAMOND))));
            voteKeys.add(new VoteKey(VoteKey.Type.ANCIENT_KEY, Lists.newArrayList(new ItemStack(Material.WHEAT)), Lists.newArrayList(new ItemStack(Material.GOLD_INGOT)), Lists.newArrayList(new ItemStack(Material.DIAMOND))));
            voteKeys.add(new VoteKey(VoteKey.Type.EPIC_KEY, Lists.newArrayList(new ItemStack(Material.WHEAT)), Lists.newArrayList(new ItemStack(Material.GOLD_INGOT)), Lists.newArrayList(new ItemStack(Material.DIAMOND))));
        }
    }

    public void save() {
        config.set("voteChests", null);
        config.set("commonItems", null);
        config.set("rareItems", null);
        config.set("veryRareItems", null);

        config.set(VoteKey.Type.OLD_KEY.toString(), getVoteKey(VoteKey.Type.OLD_KEY));
        config.set(VoteKey.Type.ANCIENT_KEY.toString(), getVoteKey(VoteKey.Type.ANCIENT_KEY));
        config.set(VoteKey.Type.EPIC_KEY.toString(), getVoteKey(VoteKey.Type.EPIC_KEY));

        List<String> serializedLocations = Lists.newArrayList();
        voteChests.forEach(block -> serializedLocations.add(UtilLoc.serialize(block.getBlock().getLocation())));

        config.set("voteChests", serializedLocations);
        config.save();
    }

    private void commandSetup() {
        BananaCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("voteKey", c -> {
           return ImmutableList.of("alter_schlüssel", "antiker_schlüssel", "epischer_schlüssel");
        });
        BananaCore.getInstance().getCommandManager().getCommandContexts().registerContext(VoteKey.Type.class, c -> {
            final String tag = c.popFirstArg();
            VoteKey.Type type = null;

            if(tag.equalsIgnoreCase("alter_schlüssel")) {
                type = VoteKey.Type.OLD_KEY;
            } else if (tag.equalsIgnoreCase("antiker_schlüssel")) {
                type = VoteKey.Type.ANCIENT_KEY;
            } else if (tag.equalsIgnoreCase("epischer_schlüssel"))
                type = VoteKey.Type.EPIC_KEY;

            if(type != null) {
                return type;
            } else
                throw new InvalidCommandArgument("Invalid VoteKey specified");
        });
    }

    public VoteKey getVoteKey(VoteKey.Type type) {
        for(VoteKey key : voteKeys)
            if (key.getType() == type)
                return key;
        return null;
    }

    public boolean isVoteChest(Block block) {
        if(block == null || block.getType() != Material.ENDER_CHEST) return false;
        return getVoteChest(block) != null;
    }

    public VoteChest getVoteChest(Block block) {
        for (VoteChest voteChest : voteChests) {
            if (voteChest.getBlock().equals(block))
                return voteChest;
        }
        return null;
    }

    public VoteChest getOccupiedVoteChest(Player player) {
        for (VoteChest voteChest : voteChests) {
            if (voteChest.isOccupied() && voteChest.getOccupier() == player.getUniqueId())
                return voteChest;
        }
        return null;
    }

}
