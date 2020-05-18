package de.pauerbanane.core.addons.plotshop.scoreboard;

import de.pauerbanane.api.scoreboards.api.Entry;
import de.pauerbanane.api.scoreboards.api.EntryBuilder;
import de.pauerbanane.api.scoreboards.api.ScoreboardHandler;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilTime;
import de.pauerbanane.core.addons.plotshop.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PurchasedPlotBoard implements ScoreboardHandler {

    private final Plot plot;

    private final OfflinePlayer owner;

    public PurchasedPlotBoard(Plot plot) {
        this.plot = plot;
        this.owner = Bukkit.getOfflinePlayer(plot.getOwner());
    }

    @Override
    public String getTitle(Player player) {
        return String.valueOf("Plot: " + plot.getRegionID());
    }

    public List<Entry> getEntries(Player player) {
        long days = Math.abs(Duration.between(LocalDateTime.now(), UtilTime.millisToLocalDate(this.owner.getLastLogin())).toDays());
        ChatColor color = ChatColor.GREEN;
        if (days < 10L) {
            color = ChatColor.GREEN;
        } else if (days >= 10L) {
            color = ChatColor.YELLOW;
        } else if (days >= 20L && days <= 30L) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.RED;
        }
        return new EntryBuilder().blank()
                .next("§fPreis: §e" + this.plot.getPrice())
                .next("§fGruppe: §e" + this.plot.getPlotGroup().getGroupID())
                .next("§fMietbar: §e" + F.tf(plot.isRentable()))
                .blank()
                .next("§fBesitzer: §a" + this.owner.getName())
                .next("§fRegion: §e" + this.plot.getRegionID())
                .blank()
                .next("§fGekauft am: §2" + this.plot.getPurchaseDateFormatted())
                .next("§fLäuft ab: §4" + (plot.isRentable() ? this.plot.getExpireDateFormatted() : "Nie"))
                .next("§fZuletzt Online: " + (this.owner.isOnline() ? "§aOnline" : (color + UtilTime.when(this.owner.getLastLogin()))))
                .blank()
                .build();
    }
}
