package de.pauerbanane.core.addons.vote.votechest.listener;

import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilPlayer;
import de.pauerbanane.core.addons.chairs.events.SitEvent;
import de.pauerbanane.core.addons.vote.votechest.VoteChest;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.gui.VoteChestGUI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class VoteChestListener implements Listener {

    private VoteChestManager manager;

    public VoteChestListener(VoteChestManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void handleChestOpen(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock().getType() != Material.CHEST) return;
        if (!manager.isVotechestChest(e.getClickedBlock())) return;
        if (manager.getOccupiedVoteChest(e.getPlayer()) == null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(F.error("Votechest", "Diese Kiste gehört dir nicht."));
            return;
        }

        VoteChest voteChest = manager.getOccupiedVoteChest(e.getPlayer());

        if(!voteChest.isChest(e.getClickedBlock())) return;
        voteChest.openChest(e.getClickedBlock());
    }

    @EventHandler
    public void handleInteract(PlayerInteractEvent e) {
        if(!manager.isVoteChest(e.getClickedBlock())) return;
        e.setCancelled(true);

        VoteChest voteChest = manager.getVoteChest(e.getClickedBlock());
        if(voteChest.isOccupied()) {
            e.getPlayer().sendMessage(F.error("Vote", "Diese VoteChest wird gerade benutzt."));
            return;
        }

        UtilPlayer.playSound(e.getPlayer(), Sound.BLOCK_CHEST_OPEN);
        SmartInventory.builder().provider(new VoteChestGUI(manager, voteChest)).title("§e§lVote-Chest").size(3).build().open(e.getPlayer());
    }

    @EventHandler
    public void handleDisconnect(PlayerQuitEvent e) {
        if(!manager.isOpeningVoteChest(e.getPlayer())) return;
        manager.getOccupiedVoteChest(e.getPlayer()).stopEvent();
    }

    @EventHandler
    public void handleTeleport(PlayerTeleportEvent e) {
        if(manager.isOpeningVoteChest(e.getPlayer()) && e.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) e.setCancelled(true);
    }

    @EventHandler
    public void handleSit(SitEvent e) {
        if(manager.isOpeningVoteChest(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void handleMovement(PlayerMoveEvent e) {
        if (!manager.isOpeningVoteChest(e.getPlayer())) return;

        if(e.getFrom().getX() != e.getTo().getX() ||
                e.getFrom().getZ() != e.getTo().getZ())
            e.setCancelled(true);
    }

}
