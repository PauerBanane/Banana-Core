package de.pauerbanane.core.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import de.pauerbanane.acf.BukkitCommandIssuer;
import de.pauerbanane.acf.ConditionFailedException;
import de.pauerbanane.acf.InvalidCommandArgument;
import de.pauerbanane.acf.PaperCommandManager;
import de.pauerbanane.api.util.UtilMath;
import de.pauerbanane.core.BananaCore;
import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.essentials.playerdata.HomeData;
import de.pauerbanane.core.data.CorePlayer;
import org.bukkit.GameMode;
import org.bukkit.Material;

import java.awt.print.Paper;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandSetup {

    private BananaCore plugin;

    private PaperCommandManager commandManager;

    public CommandSetup(BananaCore plugin) {
        this.plugin = plugin;
        this.commandManager = plugin.getCommandManager();
    }

    public void registerCommandCompletions() {
        commandManager.getCommandCompletions().registerCompletion("userhomes", c -> {
            HomeData homeData = CorePlayer.get(c.getPlayer().getUniqueId()).getData(HomeData.class);
            return ImmutableList.copyOf(homeData.getHomes().keySet());
        });
        final Stream<String> stream = Arrays.stream(GameMode.values()).map(mode -> String.valueOf(mode.toString()));
        commandManager.getCommandCompletions().registerCompletion("gamemode", c -> {
            return ImmutableList.<String>builder().addAll(stream.collect(Collectors.toList())).build();
        });
        commandManager.getCommandCompletions().registerCompletion("addon", c -> {
            ImmutableList.Builder<String> builder = ImmutableList.builder();
            plugin.getAddonManager().getAddons().forEach(addon -> builder.add(addon.getName()));
            return builder.build();
        });
    }

    public void registerCommandContexts() {
        commandManager.getCommandContexts().registerContext(GameMode.class, context -> {
            final String tag = context.popFirstArg();
            if(GameMode.valueOf(tag) != null) {
                return GameMode.valueOf(tag);
            } else if(UtilMath.isInt(tag)) {
                switch(Integer.parseInt(tag)) {
                    case 0:
                        return GameMode.SURVIVAL;
                    case 1:
                        return GameMode.CREATIVE;
                    case 2:
                        return GameMode.ADVENTURE;
                    case 3:
                        return GameMode.SPECTATOR;
                    default:
                        throw new InvalidCommandArgument("Invalid GameMode specified.");
                }
            } else
                throw new InvalidCommandArgument("Invalid GameMode specified.");
        });

        commandManager.getCommandContexts().registerContext(Addon.class, c -> {
            final String tag = c.popFirstArg();
            if(plugin.getAddonManager().getAddon(tag) != null) {
                return plugin.getAddonManager().getAddon(tag);
            } else
                throw new InvalidCommandArgument("Invalid Addon specified.");
        });

    }

    public void registerCommandConditions() {
        commandManager.getCommandConditions().addCondition("iteminhand", context -> {
            BukkitCommandIssuer issuer = (BukkitCommandIssuer)context.getIssuer();
            if (issuer.isPlayer()) {
                if (issuer.getPlayer().getInventory().getItemInMainHand() == null || issuer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR)
                    throw new ConditionFailedException("Du musst ein Item in der Hand halten.");
            } else {
                throw new ConditionFailedException("Cannt be executed by console");
            }
        });
    }

}
