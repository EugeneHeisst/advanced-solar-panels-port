package com.chocohead.advsolar.recipe;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.chocohead.advsolar.ref.AspRecipes;
import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
public record MolecularRecipe(Ingredient input,int count,ItemStack output,String outputTag,long energy) implements Recipe<SingleRecipeInput>{
    public boolean matches(SingleRecipeInput in,Level l){return in.item().getCount()>=count&&input.test(in.item())&&!result(l.registryAccess()).isEmpty();}
    public ItemStack result(HolderLookup.Provider r){if(!output.isEmpty())return output.copy();if(outputTag.isEmpty())return ItemStack.EMPTY;var tag=r.lookupOrThrow(Registries.ITEM).get(TagKey.create(Registries.ITEM,ResourceLocation.parse(outputTag)));return tag.isPresent()?tag.get().stream().findFirst().map(h->new ItemStack(h)).orElse(ItemStack.EMPTY):ItemStack.EMPTY;}
    public ItemStack assemble(SingleRecipeInput i,HolderLookup.Provider r){return result(r);}public ItemStack getResultItem(HolderLookup.Provider r){return result(r);}public boolean canCraftInDimensions(int w,int h){return true;}public RecipeSerializer<?> getSerializer(){return AspRecipes.SERIALIZER.get();}public RecipeType<?> getType(){return AspRecipes.MOLECULAR.get();}public NonNullList<Ingredient> getIngredients(){return NonNullList.of(Ingredient.EMPTY,input);}
    public static final class Serializer implements RecipeSerializer<MolecularRecipe>{
        private static final MapCodec<MolecularRecipe> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(MolecularRecipe::input),Codec.intRange(1,64).optionalFieldOf("count",1).forGetter(MolecularRecipe::count),ItemStack.CODEC.optionalFieldOf("output",ItemStack.EMPTY).forGetter(MolecularRecipe::output),Codec.STRING.optionalFieldOf("output_tag","").forGetter(MolecularRecipe::outputTag),Codec.LONG.validate(n->n>0?DataResult.success(n):DataResult.error(()->"energy must be positive")).fieldOf("energy").forGetter(MolecularRecipe::energy)).apply(i,MolecularRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf,MolecularRecipe> STREAM=StreamCodec.composite(Ingredient.CONTENTS_STREAM_CODEC,MolecularRecipe::input,ByteBufCodecs.VAR_INT,MolecularRecipe::count,ItemStack.OPTIONAL_STREAM_CODEC,MolecularRecipe::output,ByteBufCodecs.STRING_UTF8,MolecularRecipe::outputTag,ByteBufCodecs.VAR_LONG,MolecularRecipe::energy,MolecularRecipe::new);
        public MapCodec<MolecularRecipe> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,MolecularRecipe> streamCodec(){return STREAM;}
    }
}
