package de.pauerbanane.core.addons.playershop.gui;

import com.google.common.collect.ImmutableMap;
import de.pauerbanane.api.smartInventory.ClickableItem;
import de.pauerbanane.api.smartInventory.SmartInventory;
import de.pauerbanane.api.smartInventory.content.InventoryContents;
import de.pauerbanane.api.smartInventory.content.InventoryProvider;
import de.pauerbanane.api.util.ItemBuilder;
import de.pauerbanane.core.addons.playershop.Shop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopVillagerProfession implements InventoryProvider {

    private final Shop shop;

    private static final ImmutableMap<Villager.Profession, ItemStack> profession = (new ImmutableMap.Builder())
            .put(Villager.Profession.ARMORER, (new ItemBuilder(Material.IRON_CHESTPLATE)).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).name("§7" + Villager.Profession.ARMORER.name()).build())
            .put(Villager.Profession.BUTCHER, (new ItemBuilder(Material.PORKCHOP)).name("§7" + Villager.Profession.BUTCHER.name()).build())
            .put(Villager.Profession.CARTOGRAPHER, (new ItemBuilder(Material.MAP)).name("§7" + Villager.Profession.CARTOGRAPHER.name()).build())
            .put(Villager.Profession.CLERIC, (new ItemBuilder(Material.PAPER)).name("§7" + Villager.Profession.CLERIC.name()).build())
            .put(Villager.Profession.FARMER, (new ItemBuilder(Material.WHEAT)).name("§7" + Villager.Profession.FARMER.name()).build())
            .put(Villager.Profession.FISHERMAN, (new ItemBuilder(Material.FISHING_ROD)).name("§7" + Villager.Profession.FISHERMAN.name()).build())
            .put(Villager.Profession.FLETCHER, (new ItemBuilder(Material.BOW)).name("§7" + Villager.Profession.FLETCHER.name()).build())
            .put(Villager.Profession.LEATHERWORKER, (new ItemBuilder(Material.LEATHER)).name("§7" + Villager.Profession.LEATHERWORKER.name()).build())
            .put(Villager.Profession.LIBRARIAN, (new ItemBuilder(Material.BOOK)).name("§7" + Villager.Profession.LIBRARIAN.name()).build())
            .put(Villager.Profession.MASON, (new ItemBuilder(Material.BRICK_SLAB)).name("§7" + Villager.Profession.MASON.name()).build())
            .put(Villager.Profession.SHEPHERD, (new ItemBuilder(Material.SHEARS)).name("§7" + Villager.Profession.SHEPHERD.name()).build())
            .put(Villager.Profession.WEAPONSMITH, (new ItemBuilder(Material.DIAMOND_SWORD)).setItemFlag(ItemFlag.HIDE_ATTRIBUTES).name("§7" + Villager.Profession.WEAPONSMITH.name()).build())
            .build();

    public ShopVillagerProfession(Shop shop) {
        this.shop = shop;
    }

    public void init(Player player, InventoryContents contents) {
        for (Map.Entry<Villager.Profession, ItemStack> entry : (Iterable<Map.Entry<Villager.Profession, ItemStack>>)profession.entrySet()) {
            contents.add(ClickableItem.of(entry.getValue(), click -> {
                this.shop.setProfession((Villager.Profession)entry.getKey());
                this.shop.updateEntity();
                SmartInventory.builder().provider(new ShopOwnerGUI(this.shop)).title("Verwalte deinen Shop").size(6).build().open(player);
            }));
        }
    }
}
