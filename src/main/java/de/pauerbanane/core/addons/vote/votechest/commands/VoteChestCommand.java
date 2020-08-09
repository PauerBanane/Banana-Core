package de.pauerbanane.core.addons.vote.votechest.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.gui.VoteKeyManageGUI;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

@CommandAlias("votechest")
@CommandPermission("command.votechest")
public class VoteChestCommand extends BaseCommand {

    private VoteChestManager manager;

    public VoteChestCommand(VoteChestManager manager) {
        this.manager = manager;
    }


    @Subcommand("add")
    public void addChest(Player sender) {
        final RayTraceResult rt = sender.rayTraceBlocks(10);
        if (rt == null || rt.getHitBlock() == null) {
            sender.sendMessage(F.error("VoteChest", "Du musst eine Enderchest anschauen."));
            return;
        }

        final Block block = rt.getHitBlock();
        if (block.getType() != Material.ENDER_CHEST) {
            sender.sendMessage(F.error("VoteChest", "Der Block, den du anschaust, muss eine Enderchest sein."));
            return;
        }

        if(manager.isVoteChest(block)) {
            sender.sendMessage(F.error("VoteChest", "Das ist bereits eine VoteChest."));
        } else {
            manager.addVoteChest(block);
            sender.sendMessage(F.main("VoteChest", "Die VoteChest wurde erstellt."));
        }
    }

    @Subcommand("remove")
    public void removeChest(Player sender) {
        final RayTraceResult rt = sender.rayTraceBlocks(10);
        if (rt == null || rt.getHitBlock() == null) {
            sender.sendMessage(F.error("VoteChest", "Du musst eine Enderchest anschauen."));
            return;
        }

        final Block block = rt.getHitBlock();
        if (block.getType() != Material.ENDER_CHEST) {
            sender.sendMessage(F.error("VoteChest", "Der Block, den du anschaust, muss eine Enderchest sein."));
            return;
        }

        if(manager.isVoteChest(block)) {
            manager.removeVoteChest(block);
            sender.sendMessage(F.main("VoteChest", "Die VoteChest wurde gelöscht.."));
            return;
        } else {
            sender.sendMessage(F.error("VoteChest", "Dieser Block ist keine VoteChest."));
            return;
        }
    }

    @Subcommand("addkey")
    @CommandCompletion("@players @voteKey")
    public void addkey(Player sender, OnlinePlayer target, VoteKey.Type type, int amount) {
        CorePlayer cp = CorePlayer.get(target.getPlayer().getUniqueId());
        VoteData voteData = cp.getData(VoteData.class);

        voteData.setVoteKeys(type, voteData.getVoteKeys(type) + amount);

        String keyName = VoteKey.getVoteKeyName(type);
        sender.sendMessage(F.main("VoteKey", "Du hast §e" + target.getPlayer().getName() + " §a" + amount + " " + keyName + " §7gegeben."));
        target.getPlayer().sendMessage(F.main("Vote", "Du erhälst §a" + amount + " " + keyName + "§7."));
    }

    @Subcommand("voteziel")
    @CommandCompletion("@voteKey")
    public void editVoteTarget(Player sender) {
        sender.sendMessage(F.main("Vote", "§eVoteziele§7:"));
        sender.sendMessage(F.main("Vote", "§8Alter Schlüssel§7: §e" + manager.getVoteziel_1() + " Votes"));
        sender.sendMessage(F.main("Vote", "§6Antiker Schlüssel§7: §e" + manager.getVoteziel_2() + " Votes"));
        sender.sendMessage(F.main("Vote", "§dEpischer Schlüssel§7: §e" + manager.getVoteziel_3() + " Votes"));
    }

    @Subcommand("voteziel set")
    @CommandCompletion("@voteKey")
    public void editVoteTarget(Player sender, @Optional VoteKey.Type type, @Optional int amount) {

            if (type == VoteKey.Type.OLD_KEY) {
                manager.setVoteziel_1(amount);
            } else if (type == VoteKey.Type.ANCIENT_KEY) {
                manager.setVoteziel_2(amount);
            } else if (type == VoteKey.Type.EPIC_KEY) {
                manager.setVoteziel_3(amount);
            }
            sender.sendMessage(F.main("Vote", "Das Voteziel für diesen Schlüssel wurde auf §e" + amount + " §7gesetzt."));

    }

    @Subcommand("editcontent")
    public void edit(Player sender) {
        SmartInventory.builder().provider(new VoteKeyManageGUI(manager)).size(3).build().open(sender);
    }

}
