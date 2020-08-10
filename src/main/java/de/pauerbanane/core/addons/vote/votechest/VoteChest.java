package de.pauerbanane.core.addons.vote.votechest;

import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.BananaCore;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.EnderChest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.UUID;

public class VoteChest {

    private int seconds = 0;

    private int taskID;

    private static final int NORTH = 0,
            EAST  = 1,
            SOUTH = 2,
            WEST = 3;

    private Block block;

    private Block[] chests;

    private Boolean[] opened;

    private BlockFace blockFace;

    private Location centerLocation;

    private UUID occupier;

    private VoteKey currentVoteKey;

    private Location oldLocation;

    public VoteChest(Block block) {
        this.block = block;
        this.chests = new Block[4];
        this.opened = new Boolean[4];
        EnderChest eChest = (EnderChest) block.getBlockData();
        this.blockFace = eChest.getFacing();
        this.centerLocation = block.getLocation().clone().toCenterLocation();
        this.centerLocation.setYaw(180);

        chests[NORTH] = block.getWorld().getBlockAt(centerLocation.clone().add(0,0,-2));
        chests[EAST]  = block.getWorld().getBlockAt(centerLocation.clone().add(-2,0,0));
        chests[SOUTH] = block.getWorld().getBlockAt(centerLocation.clone().add(0,0,2));
        chests[WEST]  = block.getWorld().getBlockAt(centerLocation.clone().add(2,0,0));
    }

    public void startEvent(Player player, VoteKey voteKey) {
        if (isOccupied()) return;
        this.currentVoteKey = voteKey;
        this.occupier = player.getUniqueId();
        this.oldLocation = player.getLocation().clone();
        for (int i = 0; i < 4; i++) {
            opened[i] = false;
        }

        block.setType(Material.AIR);
        player.teleport(centerLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        UtilPlayer.playSound(player, Sound.ENTITY_PLAYER_LEVELUP);

        seconds = 0;
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(BananaCore.getInstance(), () -> {
            switch (seconds) {
                case 0:
                    spawnChests(voteKey, NORTH);
                    break;
                case 1:
                    spawnChests(voteKey, EAST);
                    break;
                case 2:
                    spawnChests(voteKey, SOUTH);
                    break;
                case 3:
                    spawnChests(voteKey, WEST);
                    break;
                case 33:
                    stopEvent();
                    break;
            }
            seconds++;
        }, 20, 20);
    }

    public void stopEvent() {
        if(!isOccupied()) return;
        Bukkit.getPlayer(occupier).teleport(oldLocation);
        Bukkit.getScheduler().cancelTask(taskID);

        despawnChests();

        occupier = null;
        oldLocation = null;
        currentVoteKey = null;
    }

    private void spawnChests(VoteKey voteKey, int facing) {
        Block block = chests[facing];
        block.setType(Material.CHEST);
        BlockFace blockFace = getBlockFaceByInt(facing);
        BlockData blockData = Bukkit.createBlockData(Material.CHEST, data -> ((Chest) data).setFacing(blockFace));

        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
        chest.update();
        Inventory inv = chest.getBlockInventory();
        ItemStack[] contents = voteKey.createInventory();
        for(int count = 0, slot = 1; count < contents.length; count++) {
            inv.setItem(slot, contents[count]);
            slot += 2;
        }

        block.setBlockData(blockData);
        block.getWorld().playSound(block.getLocation(), Sound.BLOCK_WOOD_PLACE, 1, 1);
        block.getWorld().playEffect(block.getLocation(), Effect.POTION_BREAK, 2);
    }

    public void openChest(Block block) {
        if(!isChest(block)) return;
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
        ItemStack veryRareItem = currentVoteKey.getEpicItem(chest.getBlockInventory());
        if(veryRareItem == null) return;

        int id = 4;
        for (int i = 0; i < 4; i++) {
            if (chests[i].equals(block))
                id = i;
        }
        if(id > 3 || opened[id]) return;

        opened[id] = true;

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§e" + Bukkit.getPlayer(occupier).getName() + " §7hat §6" + veryRareItem.getType().toString() + " §7durch " + currentVoteKey.getDisplayName() + " §7gefunden.");
        Bukkit.broadcastMessage("");

        spawnFireworks(block.getLocation(), 1);
    }

    public boolean isChest(Block block) {
        if(!isOccupied()) return false;
        if(!(block.getState() instanceof org.bukkit.block.Chest)) return false;
        for (Block block1 : chests)
            if (block1.equals(block))
                return true;

        return false;
    }

    private BlockFace getBlockFaceByInt(int i) {
        switch (i) {
            case 0:
                return BlockFace.SOUTH;
            case 1:
                return BlockFace.EAST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.WEST;
        };
        return BlockFace.SOUTH;
    }

    private void despawnChests() {
        for (Block block : chests) {
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) block.getState();
            chest.getBlockInventory().clear();
            block.setType(Material.AIR);
        }
        block.setType(Material.ENDER_CHEST);
        BlockData blockData = Bukkit.createBlockData(Material.ENDER_CHEST, data -> ((EnderChest) data).setFacing(blockFace));
        block.setBlockData(blockData);
    }

    public boolean isOccupied() {
        return occupier != null;
    }

    public Block getBlock() {
        return block;
    }

    public UUID getOccupier() {
        return occupier;
    }

    private void spawnFireworks(Location location, int amount){
        Location loc = location;
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.LIME).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

}
