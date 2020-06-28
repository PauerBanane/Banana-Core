package de.pauerbanane.core.addons.chairs;

import com.google.common.collect.Maps;
import de.pauerbanane.api.addons.Addon;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.chairs.commands.SitCommand;
import de.pauerbanane.core.addons.chairs.events.SitEvent;
import de.pauerbanane.core.addons.chairs.events.UnsitEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Chairs extends Addon {

    public HashMap<UUID, Chair> registeredChairs;

    private String sitMessage;

    private String unsitMessage;

    @Override
    public void onEnable() {
        this.registeredChairs = Maps.newHashMap();
        this.sitMessage = "Du hast dich hingesetzt.";
        this.unsitMessage = "Du bist wieder aufgestanden.";

        registerCommand(new SitCommand(this));

        registerListener(new SitListener(this));
    }

    @Override
    public void onDisable() {
        for(UUID id : registeredChairs.keySet())
            toggleSit(Bukkit.getPlayer(id), null);
    }

    @Override
    public void onReload() {

    }

    public void toggleSit(Player player, Block block) {
        if(!isSitting(player)) {
            if(isOccupied(block)) {
                player.sendMessage(F.main("Sit", "Dieser Platz ist bereits besetzt."));
                return;
            }
            Location location = player.getLocation().clone().subtract(0.0D, 1.7D, 0.0D);
            if(block != null)
                location = getSitLocation(block);
            ArmorStand seat = (ArmorStand) player.getLocation().getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            seat.setVisible(false);
            seat.setGravity(false);
            seat.setAI(false);
            seat.setCollidable(false);

            SitEvent sitEvent = new SitEvent(player, seat, sitMessage);
            sitEvent.callEvent();
            if(sitEvent.isCancelled()) {
                seat.remove();
                return;
            }

            player.sendMessage(F.main("Sit", sitMessage));
            seat.addPassenger(player);
            registeredChairs.put(player.getUniqueId(), new Chair(player.getUniqueId(), seat.getUniqueId(), block, player.getLocation()));
        } else {
            Chair chair = registeredChairs.get(player.getUniqueId());
            ArmorStand seat = chair.getArmorStand();

            new UnsitEvent(player, seat, unsitMessage).callEvent();

            player.sendMessage(F.main("Sit", unsitMessage));
            registeredChairs.remove(player.getUniqueId());
            seat.remove();
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.teleport(chair.getOldLocation());
            }, 0);
        }
    }

    public boolean isSeat(ArmorStand armorStand) {
        for(Chair chair : registeredChairs.values())
            if(chair.getArmorStand().getUniqueId().equals(armorStand.getUniqueId()))
                return true;

        return false;
    }

    public boolean isSitting(Player player) {
        return registeredChairs.containsKey(player.getUniqueId());
    }

    public boolean isOccupied(Block block) {
        if(block == null) return false;
        for(Chair chair : registeredChairs.values())
            if(chair.getBlock() != null && chair.getBlock().equals(block))
                return true;

        return false;
    }

    public Chair getChairBySeat(ArmorStand armorStand) {
        for(Chair chair : registeredChairs.values())
            if(chair.getArmorStand().getUniqueId().equals(armorStand.getUniqueId()))
                return chair;

        return null;
    }

    private Location getSitLocation(Block block) {
        Location loc = block.getLocation().add(0.5D, -1.2D, 0.5D);

        Stairs stairs = (Stairs) block.getBlockData();
        switch (stairs.getFacing().getOppositeFace()) {
            case NORTH:
                loc.setYaw(180);
                break;
            case EAST:
                loc.setYaw(-90);
                break;
            case SOUTH:
                loc.setYaw(0);
                break;
            case WEST:
                loc.setYaw(90);
                break;
            default:
                break;
        }

        return loc;
    }
}
