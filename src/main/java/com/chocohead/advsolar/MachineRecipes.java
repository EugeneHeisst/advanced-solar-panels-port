package com.chocohead.advsolar;

import com.chocohead.advsolar.ref.AspItems;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import ic2.core.ref.Ic2Blocks;
import ic2.core.ref.Ic2Items;
import net.minecraft.world.item.ItemStack;

/** Recipes.addMachineRecipes of the original: iridium and uranium ingots from the IC2 compressor and metal former. */
final class MachineRecipes {
    private MachineRecipes() {}

    static void add() {
        compress(new RecipeInputItemStack(new ItemStack(Ic2Items.get("iridium_ore"))), AspItems.IRIDIUM_INGOT.get().getDefaultInstance());
        if (Recipes.metalformerExtruding != null) Recipes.metalformerExtruding.addRecipe(new RecipeInputItemStack(new ItemStack(Ic2Items.get("iridium_plate"))), null, AspItems.IRIDIUM_INGOT.get().getDefaultInstance());
        compress(new RecipeInputItemStack(new ItemStack(Ic2Blocks.URANIUM_ORE.get())), AspItems.URANIUM_INGOT.get().getDefaultInstance());
        compress(new RecipeInputItemStack(new ItemStack(Ic2Blocks.DEEPSLATE_URANIUM_ORE.get())), AspItems.URANIUM_INGOT.get().getDefaultInstance());
        compress(new RecipeInputItemStack(new ItemStack(Ic2Items.get("crushed_uranium_ore"))), AspItems.URANIUM_INGOT.get().getDefaultInstance());
        compress(new RecipeInputItemStack(new ItemStack(Ic2Items.get("purified_crushed_uranium_ore"))), AspItems.URANIUM_INGOT.get().getDefaultInstance());
        compress(new RecipeInputItemStack(new ItemStack(Ic2Items.get("uranium_fuel"))), AspItems.URANIUM_INGOT.get().getDefaultInstance());
    }

    private static void compress(IRecipeInput input, ItemStack output) {
        // IC2 already compresses some of these (iridium ore to the plate...): the original added its recipes without replacing
        for (var existing : Recipes.compressor.getRecipes().keySet()) for (ItemStack s : input.getInputs()) if (existing.matches(s)) { AdvancedSolarPanels.LOG.info("Compressor already handles {}, keeping the IC2 recipe", s); return; }
        Recipes.compressor.addRecipe(input, null, output);
    }
}
