package de.pauerbanane.core.addons.essentials.commands;

import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.addons.essentials.Essentials;
import de.pauerbanane.core.sql.DatabaseManager;
import net.citizensnpcs.api.command.Command;
import org.bukkit.entity.Player;

import java.util.List;

@CommandAlias("vote")
public class VoteCommand extends BaseCommand {

    private List<String> messages;

    public VoteCommand(Essentials addon) {
        messages = Lists.newArrayList();
        if(!addon.getPlugin().getConfig().isSet("voteMessage")) {
            addon.getPlugin().getConfig().set("voteMessage", Lists.newArrayList("Klicke auf den Link, um für §eKnicksCraft §7zu voten:", "§ehttps://minecraft-server.eu/server/index/1FBAD"));
            addon.getPlugin().saveConfig();
        }

        messages = addon.getPlugin().getConfig().getStringList("voteMessage");
    }

    @Default
    public void send(Player sender) {
        messages.forEach(message -> sender.sendMessage(F.main("Vote", message)));
    }

    @Subcommand("heute")
    public void getDailyVotes(Player sender) {
        int amount = DatabaseManager.getInstance().getVotesToday();
        sender.sendMessage(F.main("Vote", "Es wurde heute §e" + amount + " §7mal gevotet."));
    }

}
