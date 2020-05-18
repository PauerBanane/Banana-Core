package de.pauerbanane.core.addons.schematicbrowser.gui;

import com.google.common.collect.Lists;
import de.pauerbanane.api.chatinput.ChatInput;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.*;
import de.pauerbanane.api.smartInventory.inventories.ConfirmationGUI;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.schematicbrowser.SchematicBrowser;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SchematicBrowserGUI implements InventoryProvider {

    private SchematicBrowser addon;

    private File folder;

    private  ArrayList<String> schematicNames = Lists.newArrayList();

    private File[] arrayOfFolders;

    private File[] arrayOfSchematics;

    public SchematicBrowserGUI(File folder) {
        this.addon = SchematicBrowser.getInstance();
        this.folder = folder;
        this.schematicNames     = Lists.newArrayList();
        this.arrayOfFolders     = folder.listFiles(File::isDirectory);
        this.arrayOfSchematics  = folder.listFiles(f -> !f.isDirectory());

        Arrays.stream(arrayOfSchematics).forEach(schematic -> schematicNames.add(String.valueOf(schematic.getName()).replace(".schem", "")));
    }

    @Override
    public void init(Player player, InventoryContents content) {
        Pagination pagination = content.pagination();
        ArrayList<ClickableItem> items = Lists.newArrayList();
        if (!folder.exists())
            player.closeInventory();

        byte b;
        int i;

        /* Schematic Ordner
         * Erstellt eine Übersicht aller Unterordner
         * Linksklick, um in den Ordner zu wechseln
         * Linksklick mit ausgewählter Schematic, um die Schematic in den Ordner zu verschieben
         */
        for (i = arrayOfFolders.length, b = 0; b < i; b++) {
            File subFolder = arrayOfFolders[b];
            items.add(ClickableItem.of(new ItemBuilder(Material.CHEST)
                    .name("§a" + subFolder.getName())
                    .lore("§e" + subFolder.listFiles(f -> !f.isDirectory()).length + " §fSchematics")
                    .build(), click -> {
                    if (click.getCursor() == null || click.getCursor().getType() == Material.AIR) {
                        SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(subFolder)).build().open(player);
                    } else if (isSchematicFile(click.getCursor())) {
                        File schemFile = getSchematicFile(click.getCursor());

                        try {
                            FileUtils.copyFileToDirectory(schemFile, subFolder);
                            schemFile.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        player.sendMessage(F.main("Schematic", "Du hast die Schematic §e" + click.getCursor().getItemMeta().getDisplayName() + " §7in den Ordner §e" + subFolder.getName() + " §7verschoben."));
                        click.getView().setCursor(new ItemStack(Material.AIR));
                        SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(folder)).build().open(player);
                }
            }));
        }

        /* Schematic Dateien
         * Erstellt eine Übersicht aller Schematics in diesem Ordner
         * Linksklick, um die Schematic in das Clipboard zu laden
         * Rechtsklick, um die Schematic auszuwählen und zu verschieben/löschen
         */
        i = 0;
        for (int j = arrayOfSchematics.length; i < j; i++) {
            File file = arrayOfSchematics[i];
            long size = FileUtils.sizeOf(file);
            String fileSize = "§aOK";
            if (size > 30000L) {
                fileSize = "§c§lKönnte crashen";
            } else if (size > 10000L)
                fileSize = "§eKönnte laggen";

            items.add(ClickableItem.of(new ItemBuilder(Material.SCAFFOLDING)
                    .name("§a" + String.valueOf(file.getName()).replace(".schem", ""))
                    .lore("§f" + FileUtils.byteCountToDisplaySize(size))
                    .lore("" + fileSize)
                    .build(), click -> {
                if(click.isLeftClick()) {
                    String debug = addon.getSchematicFolder().toURI().relativize(file.toURI()).getPath();
                    Bukkit.dispatchCommand((CommandSender) player, "/schem load " + debug);
                    player.closeInventory();
                } else if(click.getCursor() == null || click.getCursor().getType() == Material.AIR) {
                    click.getWhoClicked().setItemOnCursor(click.getCurrentItem());
                }
            }));
        }

        /* Buttons
         * Pagination Buttons, um alle Schematics anzeigen zu können
         * Der Hauptmenü Button führt zurück in das Schematic Hauptverzeichnis
         */
        ClickableItem[] c = new ClickableItem[items.size()];
        c = items.<ClickableItem>toArray(c);
        pagination.setItems(c);
        pagination.setItemsPerPage(27);
        if (items.size() > 0 && !pagination.isLast())
            content.set(4, 7, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite vor").build(), e -> {
                content.inventory().open(player, pagination.next().getPage());
            }));
        if (!pagination.isFirst())
            content.set(4, 1, ClickableItem.of((new ItemBuilder(Material.ARROW)).name("§f§lSeite zurück").build(), e -> {
                content.inventory().open(player, pagination.previous().getPage());
            }));

        content.set(4, 4, ClickableItem.of((new ItemBuilder(Material.MAP)).name("§f§lZurück zum §aHauptmenü").build(), e -> {
            SmartInventory.builder().title("Schematic Browser").size(5, 9)
                    .provider(new SchematicBrowserGUI(addon.getSchematicFolder()))
                    .build().open(player);
        }));

        /* Ordner erstellen
         * Erstellt einen Ordner mit dem gewünschten Namen im ausgewählten Verzeichnis
         * Der Name kann vom Spieler per ChatInput vergeben werden
         */
        content.set(4, 1, ClickableItem.of(new ItemBuilder(Material.EMERALD)
        .name("§aOrdner erstellen")
        .build(), click -> {
            new ChatInput(player, "§7Gib einen Namen für den §aOrdner §7ein:", t -> {
               File newFolder = new File(folder, t);
               newFolder.mkdirs();
               SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(folder)).build().open(player);
               player.sendMessage(F.main("Schematic", "Der Ordner §a" + t + " §7wurde erstellt."));
            });
        }));

        /* Vorheriger Ordner
         * Mit diesem Button kehrt man in das übergeordnete Verzeichnis zurück
         * Der Button wird nur angezeigt, wenn man sich nicht im Hauptverzeichnis befindet
         */
        if(!folder.getAbsolutePath().equalsIgnoreCase(addon.getSchematicFolder().getAbsolutePath())) {
            content.set(4, 6, ClickableItem.of(new ItemBuilder(Material.BARREL)
                    .name("§eVorheriger Ordner")
                    .build(), click -> {
                File prevFolder = folder.getParentFile();
                if(click.getCursor() == null || click.getCursor().getType() == Material.AIR) {
                    SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(prevFolder)).build().open(player);
                } else if (isSchematicFile(click.getCursor())) {
                File schemFile = getSchematicFile(click.getCursor());

                try {
                    FileUtils.copyFileToDirectory(schemFile, prevFolder);
                    schemFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                player.sendMessage(F.main("Schematic", "Du hast die Schematic §e" + click.getCursor().getItemMeta().getDisplayName() + " §7in den Ordner §e" + prevFolder.getName() + " §7verschoben."));
                click.getView().setCursor(new ItemStack(Material.AIR));
                SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(folder)).build().open(player);
            }
            }));
        }

        /* Löschen
         * Mit diesem Button kann ein komplettes Verzeichnis
         * oder eine einzelne Schematic gelöscht werden
         * Linksklick, um das gesamte Verzeichnis zu löschen
         * Linksklick mit ausgewählter Schematic, um die Schematic zu löschen
         */
        content.set(4, 2, ClickableItem.of(new ItemBuilder(Material.TNT)
        .name("§cLöschen")
        .lore("§eLinksklick §7um den gesamten Ordner zu §clöschen")
        .lore("§eLinksklick §7mit einer ausgewählten §aSchematic")
        .lore("§7um die Schematic zu §clöschen")
        .build(), click -> {
            if(click.isLeftClick()) {
                if(click.getCursor() == null || click.getCursor().getType() == Material.AIR) {
                    ConfirmationGUI.open(player, "§cLöschen §fbestätigen", bool -> {
                        if(bool) {
                            if(!folder.getAbsolutePath().equalsIgnoreCase(addon.getSchematicFolder().getAbsolutePath())) {
                                File prevFolder = folder.getParentFile();
                                player.sendMessage(F.main("Schematic", "Der Ordner §a" + folder.getName() + " §7wurde §cgelöscht§7."));
                                folder.delete();
                                SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(prevFolder)).build().open(player);
                            } else
                                player.sendMessage(F.error("Schematics", "Du kannst das Hauptverzeichnis nicht löschen."));
                        }
                    });
                } else if(isSchematicFile(click.getCursor())) {
                    File schematicFile = getSchematicFile(click.getCursor());
                    String name = String.valueOf(schematicFile.getName()).replace(".schem", "");
                    click.getView().setCursor(new ItemStack(Material.AIR));
                    ConfirmationGUI.open(player, "§cLöschen §fbestätigen", bool -> {
                        if (bool) {
                            schematicFile.delete();
                            player.sendMessage(F.main("Schematic", "Die Schematic §a" + name + " §7wurde §cgelöscht§7."));
                            SmartInventory.builder().title("§2Schematic Browser").size(5, 9).provider(new SchematicBrowserGUI(folder)).build().open(player);
                        }
                    });
                }
            }
        }));

        SlotIterator slotIterator = content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(0));
        slotIterator = slotIterator.allowOverride(false);
        pagination.addToIterator(slotIterator);
    }

    private boolean isSchematicFile(ItemStack item) {
        return schematicNames.contains(String.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName())));
    }

    private File getSchematicFile(ItemStack item) {
        File[] array = folder.listFiles(f -> f.getName().equalsIgnoreCase(String.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()) + ".schem")));
        return array[0];
    }
}
