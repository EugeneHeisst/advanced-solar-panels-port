package com.chocohead.advsolar.ref;
import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.recipe.MolecularRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.registries.DeferredRegister;
public final class AspRecipes {
    public static final DeferredRegister<RecipeType<?>> TYPES=DeferredRegister.create(Registries.RECIPE_TYPE,AdvancedSolarPanels.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS=DeferredRegister.create(Registries.RECIPE_SERIALIZER,AdvancedSolarPanels.MODID);
    public static final Supplier<RecipeType<MolecularRecipe>> MOLECULAR=TYPES.register("molecular_transformer",()->new RecipeType<>(){public String toString(){return "advanced_solar_panels:molecular_transformer";}});
    public static final Supplier<RecipeSerializer<com.chocohead.advsolar.recipe.ColourRecipes.Shaped>> COLOUR_SHAPED=SERIALIZERS.register("colour_shaped",com.chocohead.advsolar.recipe.ColourRecipes.ShapedSerializer::new);
    public static final Supplier<RecipeSerializer<com.chocohead.advsolar.recipe.ColourRecipes.Shapeless>> COLOUR_SHAPELESS=SERIALIZERS.register("colour_shapeless",com.chocohead.advsolar.recipe.ColourRecipes.ShapelessSerializer::new);
    public static final Supplier<RecipeSerializer<MolecularRecipe>> SERIALIZER=SERIALIZERS.register("molecular_transformer",MolecularRecipe.Serializer::new);
}
