package de.pauerbanane.core.addons.ranks.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.core.addons.ranks.Rank;
import de.pauerbanane.core.addons.ranks.RankManager;
import de.pauerbanane.core.addons.ranks.Ranks;
import de.pauerbanane.core.addons.ranks.gui.RankGUI;
import org.bukkit.entity.Player;

@CommandAlias("rang")
public class RankUserCommands extends BaseCommand {

    private Ranks addon;
    private RankManager manager;

    public RankUserCommands(Ranks addon) {
        this.addon = addon;
        this.manager = addon.getManager();
    }

    @Default
    public void checkRank(Player sender) {
        SmartInventory.builder().provider(new RankGUI(manager)).size(3).title("§eRangübersicht").build().open(sender);
    }

}
