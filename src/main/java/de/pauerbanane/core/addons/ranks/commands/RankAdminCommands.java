package de.pauerbanane.core.addons.ranks.commands;

import com.comphenix.protocol.PacketType;
import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.ranks.Rank;
import de.pauerbanane.core.addons.ranks.Ranks;
import de.pauerbanane.core.addons.ranks.conditions.GroupCondition;
import de.pauerbanane.core.addons.ranks.conditions.PlaytimeCondition;
import de.pauerbanane.core.addons.ranks.conditions.RankCondition;
import de.pauerbanane.core.addons.ranks.conditions.VoteCondition;
import de.pauerbanane.core.addons.ranks.gui.admin.AdminRemoveRankConditionGUI;
import de.pauerbanane.core.addons.votifier.Vote;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import scala.concurrent.impl.FutureConvertersImpl;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("rang")
@CommandPermission("command.rang.admin")
public class RankAdminCommands extends BaseCommand {

    private Ranks addon;

    public RankAdminCommands(Ranks addon) {
        this.addon = addon;
    }

    @Subcommand("admin list")
    public void listRanks(Player sender) {
        sender.sendMessage(F.main("Rang", addon.getManager().getRanks().keySet().toString()));
    }

    @Subcommand("admin create")
    @CommandCompletion("@permissionGroup @material")
    public void create(Player sender, Group group, Material material) {
        if (addon.getManager().getRank(group.getName()) != null) {
            sender.sendMessage(F.error("Rang", "Dieser Rang existiert bereits."));
            return;
        }
        Rank rank = new Rank(group, material, null);

        if (addon.getManager().registerRank(rank)) {
            sender.sendMessage(F.main("Rang", "Der Rang wurde erstellt."));
        } else
            sender.sendMessage(F.error("Rang", "Der Rang konnte nicht erstellt werden."));
    }

    @Subcommand("admin remove")
    @CommandCompletion("@rank")
    public void removeRank(Player sender, Rank rank) {
        addon.getManager().removeRank(rank);
        sender.sendMessage(F.main("Rang", "Der Rang wurde entfernt."));
    }

    @Subcommand("admin addCondition")
    @CommandCompletion("@rank @rankConditionType @nothing")
    public void addCondition(Player sender, Rank rank, RankCondition.TYPE type, String value) {
        if (!RankCondition.isValidValue(type,value)) {
            sender.sendMessage(F.error("Rang", "Dieser Wert ist nicht möglich für diese Voraussetzung."));
            return;
        }

        if (type == RankCondition.TYPE.GROUP_CONDITION) {
            rank.addCondition(new GroupCondition(PermissionManager.getApi().getGroupManager().getGroup(value)));
        } else if (type == RankCondition.TYPE.PLAYTIME_CONDITION) {
            rank.addCondition(new PlaytimeCondition(Integer.parseInt(value)));
        } else if (type == RankCondition.TYPE.VOTE_CONDITION) {
            rank.addCondition(new VoteCondition(Integer.parseInt(value)));
        } else {
            sender.sendMessage(F.error("Rang", "Diese Voraussetzung wird noch nicht unterstützt."));
            return;
        }

        sender.sendMessage(F.main("Rang", "Die Voraussetzung wurde dem Rang hinzugefügt."));
    }

    @Subcommand("admin removeCondition")
    @CommandCompletion("@rank")
    public void removeCondition(Player sender, Rank rank) {
        SmartInventory.builder().provider(new AdminRemoveRankConditionGUI(rank)).size(3).title("§f" + rank.getGroup().getDisplayName() + "§8: §eVoraussetzungen").build().open(sender);
    }

}
