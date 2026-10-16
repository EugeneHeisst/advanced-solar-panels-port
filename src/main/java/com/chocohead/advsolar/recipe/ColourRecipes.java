package com.chocohead.advsolar.recipe;
import ic2.api.item.ElectricItem;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.*;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.chocohead.advsolar.ref.AspRecipes;
public final class ColourRecipes {
    static ItemStack inherit(ItemStack result,CraftingInput input){double charge=0;for(var stack:input.items()){charge+=ElectricItem.manager.getCharge(stack);if(stack.has(DataComponents.DYED_COLOR)&&result.getItem() instanceof com.chocohead.advsolar.items.ItemArmourSolarHelmet helmet&&helmet.canBeDyed()&&!result.has(DataComponents.DYED_COLOR))result.set(DataComponents.DYED_COLOR,stack.get(DataComponents.DYED_COLOR));}if(charge>0)ElectricItem.manager.charge(result,charge,Integer.MAX_VALUE,true,false);return result;}
    public static class Shaped extends ShapedRecipe {
        final ShapedRecipePattern pattern;final ItemStack output;
        public Shaped(String group,CraftingBookCategory category,ShapedRecipePattern pattern,ItemStack result){super(group,category,pattern,result,true);this.pattern=pattern;output=result;}
        public ItemStack assemble(CraftingInput input,HolderLookup.Provider r){return inherit(super.assemble(input,r),input);}public RecipeSerializer<?> getSerializer(){return AspRecipes.COLOUR_SHAPED.get();}
    }
    public static class Shapeless extends ShapelessRecipe {
        final ItemStack output;
        public Shapeless(String group,CraftingBookCategory category,ItemStack result,NonNullList<Ingredient> input){super(group,category,result,input);output=result;}
        public ItemStack assemble(CraftingInput input,HolderLookup.Provider r){return inherit(super.assemble(input,r),input);}public RecipeSerializer<?> getSerializer(){return AspRecipes.COLOUR_SHAPELESS.get();}
    }
    private static NonNullList<Ingredient> list(java.util.List<Ingredient> l){var n=NonNullList.<Ingredient>create();n.addAll(l);return n;}
    public static class ShapedSerializer implements RecipeSerializer<Shaped>{
        static final MapCodec<Shaped> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Codec.STRING.optionalFieldOf("group","").forGetter(Shaped::getGroup),CraftingBookCategory.CODEC.optionalFieldOf("category",CraftingBookCategory.MISC).forGetter(Shaped::category),ShapedRecipePattern.MAP_CODEC.forGetter(s->s.pattern),ItemStack.STRICT_CODEC.fieldOf("result").forGetter(s->s.output)).apply(i,Shaped::new));
        static final StreamCodec<RegistryFriendlyByteBuf,Shaped> STREAM=StreamCodec.composite(ByteBufCodecs.STRING_UTF8,Shaped::getGroup,CraftingBookCategory.STREAM_CODEC,Shaped::category,ShapedRecipePattern.STREAM_CODEC,s->s.pattern,ItemStack.STREAM_CODEC,s->s.output,Shaped::new);
        public MapCodec<Shaped> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,Shaped> streamCodec(){return STREAM;}
    }
    public static class ShapelessSerializer implements RecipeSerializer<Shapeless>{
        static final MapCodec<Shapeless> CODEC=RecordCodecBuilder.mapCodec(i->i.group(Codec.STRING.optionalFieldOf("group","").forGetter(Shapeless::getGroup),CraftingBookCategory.CODEC.optionalFieldOf("category",CraftingBookCategory.MISC).forGetter(Shapeless::category),ItemStack.STRICT_CODEC.fieldOf("result").forGetter(s->s.output),Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").xmap(ColourRecipes::list,l->l).forGetter(Shapeless::getIngredients)).apply(i,Shapeless::new));
        static final StreamCodec<RegistryFriendlyByteBuf,Shapeless> STREAM=StreamCodec.composite(ByteBufCodecs.STRING_UTF8,Shapeless::getGroup,CraftingBookCategory.STREAM_CODEC,Shapeless::category,ItemStack.STREAM_CODEC,s->s.output,Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()).map(ColourRecipes::list,l->l),Shapeless::getIngredients,Shapeless::new);
        public MapCodec<Shapeless> codec(){return CODEC;}public StreamCodec<RegistryFriendlyByteBuf,Shapeless> streamCodec(){return STREAM;}
    }
}
