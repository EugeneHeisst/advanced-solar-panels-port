package com.chocohead.advsolar.jei;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.recipe.MolecularRecipe;
import com.chocohead.advsolar.ref.*;
import mezz.jei.api.*;
import mezz.jei.api.registration.*;
import mezz.jei.api.recipe.*;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.*;
@JeiPlugin
public class AspJeiPlugin implements IModPlugin {
    public static final RecipeType<MolecularRecipe> TYPE=RecipeType.create(AdvancedSolarPanels.MODID,"molecular_transformer",MolecularRecipe.class);
    private static mezz.jei.api.runtime.IJeiRuntime runtime;
    public ResourceLocation getPluginUid(){return AdvancedSolarPanels.id("jei");}
    public void onRuntimeAvailable(mezz.jei.api.runtime.IJeiRuntime r){runtime=r;}
    public static boolean showForAudit(){if(runtime==null)return false;runtime.getRecipesGui().showTypes(List.of(TYPE));return true;}
    public void registerCategories(IRecipeCategoryRegistration r){r.addRecipeCategories(new Category(r.getJeiHelpers().getGuiHelper()));}
    public void registerRecipes(IRecipeRegistration r){var level=Minecraft.getInstance().level;if(level!=null)r.addRecipes(TYPE,level.getRecipeManager().getAllRecipesFor(AspRecipes.MOLECULAR.get()).stream().map(h->h.value()).filter(recipe->!recipe.result(level.registryAccess()).isEmpty()&&recipe.input().getItems().length>0).toList());}
    public void registerRecipeCatalysts(IRecipeCatalystRegistration r){r.addRecipeCatalyst(new ItemStack(AspBlocks.MOLECULAR_TRANSFORMER.get()),TYPE);}
    static class Category implements IRecipeCategory<MolecularRecipe>{
        private final IDrawable icon;
        Category(mezz.jei.api.helpers.IGuiHelper gui){icon=gui.createDrawableItemLike(AspBlocks.MOLECULAR_TRANSFORMER.get());}
        public RecipeType<MolecularRecipe> getRecipeType(){return TYPE;}public Component getTitle(){return Component.translatable("block.advanced_solar_panels.molecular_transformer");}public IDrawable getIcon(){return icon;}public int getWidth(){return 155;}public int getHeight(){return 54;}
        public void setRecipe(IRecipeLayoutBuilder b,MolecularRecipe r,IFocusGroup focus){b.addInputSlot(5,7).setStandardSlotBackground().addItemStacks(Arrays.stream(r.input().getItems()).map(s->s.copyWithCount(r.count())).toList());b.addOutputSlot(123,7).setOutputSlotBackground().addItemStack(r.result(Minecraft.getInstance().level.registryAccess()));}
        public void draw(MolecularRecipe r,IRecipeSlotsView slots,GuiGraphics g,double x,double y){var f=Minecraft.getInstance().font;g.drawString(f,"→",73,11,0xff404040,false);g.drawString(f,String.format(Locale.ROOT,"%,d EU",r.energy()),5,35,0xff404040,false);}
    }
}
