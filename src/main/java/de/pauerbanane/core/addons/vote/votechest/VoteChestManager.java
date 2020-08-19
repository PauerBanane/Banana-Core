package de.pauerbanane.core.addons.vote.votechest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.acf.PaperCommandManager;
import de.pauerbanane.api.util.*;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.vote.VoteAddon;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.commands.VoteChestCommand;
import de.pauerbanane.core.addons.vote.votechest.listener.VoteChestListener;
import de.pauerbanane.core.data.CorePlayer;
import de.pauerbanane.core.sql.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoteChestManager {

    private HashMap<String, VoteKey> voteKeys;

    private ArrayList<VoteChest> voteChests;

    private static VoteChestManager instance;

    private VoteAddon addon;

    private PaperCommandManager commandManager;

    private FileLoader config;

    public VoteChestManager(VoteAddon addon, PaperCommandManager commandManager) {
        instance = this;
        this.addon = addon;
        this.commandManager = commandManager;
        this.voteKeys = Maps.newHashMap();
        this.voteChests = Lists.newArrayList();
        ConfigurationSerialization.registerClass(VoteChestContent.class, "votechestcontent");
        ConfigurationSerialization.registerClass(VoteKey.class, "votekey");

        addon.registerListener(new VoteChestListener(this));
        load();
    }

    public boolean registerVoteKey(VoteKey voteKey) {
        if (this.voteKeys.containsKey(voteKey.getName())) return false;
        this.voteKeys.put(voteKey.getName(), voteKey);
        return true;
    }

    public void deleteVoteKey(VoteKey voteKey) {
        this.voteKeys.remove(voteKey.getName());
    }

    private void load() {
        this.config = new FileLoader(addon.getAddonFolder(), "VoteChests.yml");
        ConfigurationSection section;
        if (config.isSet("votekeys")) {
            section = config.getConfigurationSection("votekeys");
            int loadedKeys = 0;
            for (String entry : section.getKeys(false)) {
                VoteKey voteKey = section.getSerializable(entry, VoteKey.class);
                registerVoteKey(voteKey);
                loadedKeys++;
            }
            addon.getPlugin().getLogger().info("Loaded " + loadedKeys + " VoteKeys");
        }

        commandManager.getCommandCompletions().registerCompletion("votekey", c -> {
            return ImmutableList.copyOf(voteKeys.keySet());
        });

        commandManager.getCommandContexts().registerContext(VoteKey.class, c -> {
            final String tag = c.popFirstArg();
            VoteKey voteKey = getVoteKey(tag);
            if (voteKey != null) {
                return voteKey;
            } else
                throw new InvalidCommandArgument("Dieser VoteKey existiert nicht.");
        });

        commandManager.registerCommand(new VoteChestCommand(this));

        if (BananaCore.getInstance().getPluginManager().getPlugin("BentoBox") != null) {
            Bukkit.getScheduler().runTaskLater(BananaCore.getInstance(), () -> {
                loadVoteChests();
            }, 20 * 10);
        } else
            loadVoteChests();
    }

    private void loadVoteChests() {
        if (config.isSet("voteChests")) {
            List<String> serializedLocations = config.getStringList("voteChests");
            serializedLocations.forEach(serializedLocation -> {
                Location location = UtilLoc.deserialize(serializedLocation);
                if (location != null && location.getWorld() != null) {
                    Block block = location.getWorld().getBlockAt(location);
                    if (block.getType() == Material.ENDER_CHEST) {
                        voteChests.add(new VoteChest(block));
                    } else
                        addon.getPlugin().getLogger().warning("Failed to load VoteChest at " + serializedLocation + ". Deleting...");
                } else
                    addon.getPlugin().getLogger().warning("Failed to load VoteChest at " + serializedLocation + ". Deleting...");
            });

            addon.getPlugin().getLogger().info("Loaded " + voteChests.size() + " Vote-Chests");
        }
    }

    public void save() {
        config.set("votekeys", null);
        for (int i = 0; i < voteKeys.values().size(); i++) {
            config.set("votekeys." + i, voteKeys.values().toArray()[i]);
        }

        config.set("voteChests", null);
        List<String> serializedLocations = Lists.newArrayList();
        voteChests.forEach(block -> serializedLocations.add(UtilLoc.serialize(block.getBlock().getLocation())));

        config.set("voteChests", serializedLocations);
        config.save();
    }

    public void checkIfVotetargetReached() {
        Bukkit.getScheduler().runTaskLater(addon.getPlugin(), () -> {
            int amount = DatabaseManager.getInstance().getVotesToday();

            for (VoteKey voteKey : voteKeys.values()) {
                if (voteKey.getRequiredVotes() == amount) {
                    giveVoteKeys(voteKey, 1);
                }
            }

        }, 20L);

    }

    public void giveVoteKeys(VoteKey voteKey, int amount) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            CorePlayer cp = CorePlayer.get(player.getUniqueId());
            VoteData voteData = cp.getData(VoteData.class);

            voteData.addVoteKey(voteKey, amount);

            player.getPlayer().sendMessage(F.main("Vote", "Du erhälst §a" + amount + " " + voteKey.getDisplayName() + "§7."));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        });
    }

    public boolean isOpeningVoteChest(Player player) {
        for(VoteChest voteChest : voteChests) {
            if (voteChest.isOccupied() && voteChest.getOccupier() == player.getUniqueId()) return true;
        }

        return false;
    }

    public void startVoteChestEvent(Player player, VoteChest voteChest, VoteKey voteKey) {
        voteChest.startEvent(player,voteKey);
    }

    public void addVoteChest(Block block) {
        voteChests.add(new VoteChest(block));
    }

    public void removeVoteChest(Block block) {
        voteChests.remove(getVoteChest(block));
    }

    public VoteKey getVoteKey(String name) {
        return voteKeys.get(name);
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

    public boolean isVotechestChest(Block block) {
        for (VoteChest voteChest : voteChests) {
            if (voteChest.isChest(block))
                return true;
        }
        return false;
    }

    public static VoteChestManager getInstance() {
        return instance;
    }

    public HashMap<String, VoteKey> getVoteKeys() {
        return voteKeys;
    }
}
