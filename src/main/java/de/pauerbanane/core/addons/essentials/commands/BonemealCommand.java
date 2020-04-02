package de.pauerbanane.core.addons.essentials.commands;

import com.google.common.collect.Lists;
import de.pauerbanane.acf.BaseCommand;
import de.pauerbanane.acf.annotation.CommandAlias;
import de.pauerbanane.acf.annotation.CommandPermission;
import de.pauerbanane.acf.annotation.Default;
import de.pauerbanane.acf.annotation.Subcommand;
import de.pauerbanane.api.util.F;
import de.pauerbanane.api.util.UtilBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("bonemeal")
@CommandPermission("command.bonemeal")
public class BonemealCommand extends BaseCommand {
    private final int max_radius = 35;

    private List<Material> flowers;

    public BonemealCommand() {
        flowers = Lists.newArrayList();

        this.flowers.add(Material.DANDELION);
        this.flowers.add(Material.BLUE_ORCHID);
        this.flowers.add(Material.RED_TULIP);
        this.flowers.add(Material.WHITE_TULIP);
        this.flowers.add(Material.FERN);
        this.flowers.add(Material.GRASS);
        this.flowers.add(Material.LARGE_FERN);
        this.flowers.add(Material.TALL_GRASS);
        this.flowers.add(Material.POPPY);
        this.flowers.add(Material.AZURE_BLUET);
        this.flowers.add(Material.OXEYE_DAISY);
        this.flowers.add(Material.LILAC);
    }

    @Subcommand("deletegrass")
    public void AntibonemealCommand(Player sender, int radius) {
        Set<Block> blocks = UtilBlock.getInRadius(sender.getLocation(), radius, 4.0D).keySet();
        int counter = 0;
        for (Block block : blocks) {
            if (this.flowers.contains(block.getType())) {
                block.setType(Material.AIR, true);
                counter++;
            }
            block.getBlockKey();
        }
        sender.sendMessage(F.main("Admin", "Es wurden " + F.elem(String.valueOf(counter)) + " Blöcke aktualisiert."));
    }

    @Default
    public void bonemealArea(Player sender, int radius) {
        Set<Block> blocks = UtilBlock.getInRadius(sender.getLocation(), radius, 4.0D).keySet();
        int counter = 0;
        if (radius > 35) {
            sender.sendMessage(F.main("Admin", "Der Radius ist zu groß."));
            return;
        }
        for (Block block : blocks) {
            if (block.getBlockData() instanceof Ageable) {
                Ageable crop = (Ageable)block.getBlockData();
                crop.setAge(crop.getMaximumAge());
                block.setBlockData((BlockData)crop, true);
                counter++;
                continue;
            }
            if (block.getType() == Material.GRASS_BLOCK && block.getRelative(BlockFace.UP).getType() == Material.AIR) {
                int chance = ThreadLocalRandom.current().nextInt(0, 100);
                Block up = block.getRelative(BlockFace.UP);
                if (chance < 80) {
                    up.setType(Material.GRASS, true);
                    counter++;
                    continue;
                }
                up.setType(this.flowers.get(ThreadLocalRandom.current().nextInt(this.flowers.size())), true);
                counter++;
            }
        }
        sender.sendMessage(F.main("Admin", "Es wurden " + F.elem(String.valueOf(counter)) + " Blöcke aktualisiert."));
    }
}