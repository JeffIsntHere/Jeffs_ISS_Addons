package jeff.iss_addons.recipes;

import io.redspace.ironsspellbooks.registries.ItemRegistry;
import jeff.iss_addons.JeffsISSAddons;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class Recipes extends RecipeProvider
{
    public Recipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output)
    {
        if (JeffsISSAddons._config._enableArcaneEssenceRecipe.get())
        {
            JeffsISSAddons.LOGGER.info("generated arcane_essence recipe");
            output.accept(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "arcane_essence"), new ShapelessRecipe(JeffsISSAddons.MODID, CraftingBookCategory.MISC, new ItemStack(ItemRegistry.ARCANE_ESSENCE), NonNullList.of(Ingredient.of(Items.BLUE_DYE), Ingredient.of(Items.PURPLE_DYE), Ingredient.of(Items.FLINT))),null);
        }
        if (JeffsISSAddons._config._enableCinderEssenceRecipe.get())
        {
            JeffsISSAddons.LOGGER.info("generated cinder_essence recipe");
            output.accept(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "cinder_essence"), new ShapelessRecipe(JeffsISSAddons.MODID, CraftingBookCategory.MISC, new ItemStack(ItemRegistry.CINDER_ESSENCE), NonNullList.of(Ingredient.of(ItemRegistry.ARCANE_ESSENCE.get()), Ingredient.of(Items.BLAZE_POWDER), Ingredient.of(Items.BROWN_DYE), Ingredient.of(Items.BLACK_DYE))),null);
        }
        if (JeffsISSAddons._config._enableCommonInkEssenceRecipe.get())
        {
            JeffsISSAddons.LOGGER.info("generated ink_common recipe");
            output.accept(ResourceLocation.fromNamespaceAndPath(JeffsISSAddons.MODID, "ink_common"), new ShapelessRecipe(JeffsISSAddons.MODID, CraftingBookCategory.MISC, new ItemStack(ItemRegistry.INK_COMMON), NonNullList.of(Ingredient.of(ItemRegistry.ARCANE_ESSENCE.get()), Ingredient.of(Items.BLACK_DYE), Ingredient.of(Items.GLASS_BOTTLE))),null);
        }
    }


}
