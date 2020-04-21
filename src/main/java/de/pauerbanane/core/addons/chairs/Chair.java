package de.pauerbanane.core.addons.chairs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;

import java.util.UUID;

public class Chair {

    private UUID owner;

    private UUID armorStandID;

    private Block occupiedBlock;

    private Location oldLocation;

    public Chair(UUID owner, UUID armorStandID, Block occupiedBlock, Location oldLocation) {
        this.owner = owner;
        this.armorStandID = armorStandID;
        this.occupiedBlock = occupiedBlock;
        this.oldLocation = oldLocation;
    }

    public ArmorStand getArmorStand() {
        return (ArmorStand) Bukkit.getEntity(armorStandID);
    }

    public Block getBlock() {
        return occupiedBlock;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getOldLocation() {
        return oldLocation;
    }
}
