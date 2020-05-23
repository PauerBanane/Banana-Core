package de.pauerbanane.core.addons.ultrahardcore;

import de.pauerbanane.core.addons.Addon;
import de.pauerbanane.core.addons.ultrahardcore.commands.TopTenCommand;
import de.pauerbanane.core.addons.ultrahardcore.listener.UHCListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Iterator;

public class UltraHardcore extends Addon {

    @Override
    public void onEnable() {
        registerListener(new UHCListener(this));

        disableRecipes();
        createRecipes();

        registerCommand(new TopTenCommand());
    }

    @Override
    public void onDisable() {

    }

    private void disableRecipes() {
        Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
        while(it.hasNext()) {
            Recipe r = it.next();
            if(r.getResult().getType() == Material.GOLDEN_APPLE ||
               r.getResult().getType() == Material.GLISTERING_MELON_SLICE) {
                it.remove();
            }
        }
    }

    private void createRecipes() {
        NamespacedKey key = new NamespacedKey(plugin, "uhc_recipe_golden_apple");
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE);
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        recipe.shape("GGG", "GAG", "GGG");
        recipe.setIngredient('G', Material.GOLD_BLOCK);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(recipe);
    }




}
