package de.pauerbanane.core.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.*;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.data.PermissionManager;
import net.luckperms.api.node.types.MetaNode;
import net.luckperms.api.node.types.PermissionNode;
import net.luckperms.api.node.types.PrefixNode;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("checkpermission")
@CommandPermission("command.checkpermission")
public class CheckPermissionCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players @nodetype @nothing @boolean @nothing @worlds")
    @Syntax("<Spieler> <NodeType> <Permission> <Value> [Server] [Welt]")
    public void check(Player sender, OfflinePlayer player, String nodeType, String key, String value, @Optional String server, @Optional String world) {
        if (nodeType.equals("permission")) {
            if (Boolean.valueOf(value) == null) {
                sender.sendMessage(F.error("Permission", "Du kannst als Value nur true|false angeben."));
                return;
            }
            PermissionNode node = PermissionManager.buildPermissionNode(key, Boolean.valueOf(value), server, world);
            sender.sendMessage(F.ctf(PermissionManager.hasNode(player.getUniqueId(), node), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission."), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission nicht.")));
        } else if (nodeType.equals("meta")) {
            MetaNode node = PermissionManager.buildMetaNode(key, value, server, world);
            sender.sendMessage(F.ctf(PermissionManager.hasNode(player.getUniqueId(), node), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission."), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission nicht.")));
        } else if(nodeType.equals("prefix")) {
            if(!UtilMath.isInt(value)) {
                sender.sendMessage(F.error("Permission", "Du musst eine Zahl als Priorität angeben."));
                return;
            }
            PrefixNode node = PermissionManager.buildPrefixNode(key, Integer.valueOf(value), server, world);
            sender.sendMessage(F.ctf(PermissionManager.hasNode(player.getUniqueId(), node), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission."), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission nicht.")));
        } else if(nodeType.equals("suffix")) {
            if(!UtilMath.isInt(value)) {
                sender.sendMessage(F.error("Permission", "Du musst eine Zahl als Priorität angeben."));
                return;
            }
            SuffixNode node = PermissionManager.buildSuffixNode(key, Integer.valueOf(value), server, world);
            sender.sendMessage(F.ctf(PermissionManager.hasNode(player.getUniqueId(), node), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission."), F.main("Permission", "§e" + player.getName() + " §7hat diese Permission nicht.")));
        } else
            sender.sendMessage(F.error("Permission", "Du hast keinen gültigen NodeType angegeben."));
    }

}
