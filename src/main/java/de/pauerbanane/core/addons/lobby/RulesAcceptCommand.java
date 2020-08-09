package de.pauerbanane.core.addons.lobby;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("accept")
public class RulesAcceptCommand extends BaseCommand {

    private Lobby addon;

    public RulesAcceptCommand(Lobby addon) {
        this.addon = addon;
    }

    @Default
    public void accept(Player sender) {
        if(addon.hasAcceptedRules(sender.getUniqueId())) {
            sender.sendMessage(F.error("Regeln", "Du hast die Regeln bereits akzeptiert."));
            return;
        }

        addon.acceptRules(sender.getUniqueId());
        sender.sendMessage(F.main("Regeln", "Du hast die Regeln §2akzeptiert§7. Viel Spaß auf §eKnicksCraft§7!"));

        User user = PermissionManager.getUser(sender.getUniqueId());
        InheritanceNode node = PermissionManager.buildInheritanceNode("player", null, null);
        PermissionManager.addNode(sender, node);
    }

}
