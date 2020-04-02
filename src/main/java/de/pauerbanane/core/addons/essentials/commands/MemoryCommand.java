package de.pauerbanane.core.addons.essentials.commands;

import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.CommandIssuer;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.api.util.F;
import org.bukkit.Bukkit;
import org.bukkit.World;

@CommandAlias("memory|mem")
@CommandPermission("command.memory")
public class MemoryCommand extends BaseCommand {
    long startuptime = System.currentTimeMillis();

    @Default
    public void showMemory(CommandIssuer sender) {
        sender.sendMessage(F.main("§cMonitor", "Das Plugin lseit: + UtilTime.getElapsedTime(this.startuptime) + "));
        sender.sendMessage(F.main("§cMonitor", "RAM verbrauch: " + F.name(String.valueOf(getUsedRam())) + "/" + F.name(String.valueOf(getMaxRam())) + " MB"));
        sender.sendMessage("§f[" + F.ProgressBar(getUsedRam(), getMaxRam(), 50, "|") + "§f]");
        sender.sendMessage("");
        for (World w : Bukkit.getWorlds()) {
            StringBuilder message = new StringBuilder();
            message.append(F.main("Monitor", w.getName()));
            message.append(" §7Chunks: §c");
            message.append((w.getLoadedChunks()).length);
            message.append(" §7Spieler: §c");
            message.append(w.getPlayerCount());
            message.append(" §7Entities: §c");
            message.append(w.getEntityCount());
            sender.sendMessage(message.toString());
        }
    }

    private static final int getFreeRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.freeMemory() / 1048576L));
    }

    private static final int getMaxRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.maxMemory() / 1048576L));
    }

    private static final int getUsedRam() {
        return getTotalRam() - getFreeRam();
    }

    private static final int getTotalRam() {
        Runtime runtime = Runtime.getRuntime();
        return Math.round((float)(runtime.totalMemory() / 1048576L));
    }
}