package de.pauerbanane.core.addons.vote.votechest.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.acf.bukkit.contexts.OnlinePlayer;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.vote.data.VoteData;
import de.pauerbanane.core.addons.vote.votechest.VoteChestManager;
import de.pauerbanane.core.addons.vote.votechest.VoteKey;
import de.pauerbanane.core.data.CorePlayer;
import net.citizensnpcs.api.command.Command;
import org.bukkit.ChatColor;
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

    @Subcommand("givevotekey")
    @CommandCompletion("@players @votekey @nothing")
    public void giveKey(Player sender, OnlinePlayer player, VoteKey voteKey, int amount) {
        VoteData voteData = CorePlayer.get(player.getPlayer().getUniqueId()).getData(VoteData.class);
        voteData.addVoteKey(voteKey, amount);
        sender.sendMessage(F.main("VoteChest", "Du hast §e" + player.getPlayer().getName() + " §a" + amount + " " + voteKey.getDisplayName() + " §7gegeben."));
        player.getPlayer().sendMessage(F.main("VoteChest", "Du hast §e" + amount + " " + voteKey.getDisplayName() + " §7erhalten."));
    }

    @Subcommand("addchest")
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

    @Subcommand("removechest")
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

    @Subcommand("createkey")
    @CommandCompletion("@nothing @nothing @material @nothing @nothing")
    @Syntax("<Name> <Anzeigename> <Material> [Votes] [Model-Data]")
    public void createKey(Player sender, String name, String displayName, Material material, @Optional int requiredVotes, @Optional int modelData) {
        VoteKey voteKey = new VoteKey(name, ChatColor.translateAlternateColorCodes('&',displayName), material, modelData, null, requiredVotes);
        if (manager.registerVoteKey(voteKey)) {
            sender.sendMessage(F.main("VoteChest", "Der VoteKey " + voteKey.getDisplayName() + " §7wurde erstellt."));
        } else
            sender.sendMessage(F.error("VoteChest", "Es existiert bereits ein VoteKey mit diesem Namen."));
    }

    @Subcommand("removekey")
    @CommandCompletion("@votekey")
    public void removeKey(Player sender, VoteKey voteKey) {
        manager.deleteVoteKey(voteKey);
        sender.sendMessage(F.main("VoteChest", "Der VoteKey wurde gelöscht."));
    }

    @Subcommand("set modeldata")
    @CommandCompletion("@votekey @nothing")
    public void setmodeldata(Player sender, VoteKey voteKey, int modelData) {
        voteKey.setModelData(modelData);
        sender.sendMessage(F.main("VoteChest", "Du hast die ModelData auf §e" + modelData + " §7gesetzt."));
    }

    @Subcommand("set material")
    @CommandCompletion("@votekey @material")
    public void setMaterial(Player sender, VoteKey voteKey, Material material) {
        voteKey.setIcon(material);
        sender.sendMessage(F.main("VoteChest", "Das Icon wurde zu §e" + material.toString() + " §7geändert."));
    }

    @Subcommand("set displayname")
    @CommandCompletion("@votekey @nothing")
    public void setName(Player sender, VoteKey voteKey, @Single String name) {
        voteKey.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        sender.sendMessage(F.main("VoteChest", "Du hast den Namen zu " + voteKey.getDisplayName() + " §7geändert."));
    }

    @Subcommand("set requiredVotes")
    @CommandCompletion("@votekey @nothing")
    public void setName(Player sender, VoteKey voteKey, int amount) {
        voteKey.setRequiredVotes(amount);
        sender.sendMessage(F.main("VoteChest", "Die Votebelohnung für " + voteKey.getDisplayName() + " §7wird nun ab §e" + voteKey.getRequiredVotes() + " Votes §7verteilt."));
    }

}
