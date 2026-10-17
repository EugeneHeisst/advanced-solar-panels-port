package com.chocohead.advsolar.client;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.gui.*;
import com.chocohead.advsolar.ref.*;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
@EventBusSubscriber(modid=AdvancedSolarPanels.MODID,value=Dist.CLIENT,bus=EventBusSubscriber.Bus.MOD)
public final class AspClient {
    @SubscribeEvent static void setup(RegisterMenuScreensEvent e){e.register(AspMenus.TILE.get(),GuiAsp::new);}
    @SubscribeEvent static void itemRenderer(net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent e){e.registerItem(new net.neoforged.neoforge.client.extensions.common.IClientItemExtensions(){private MolecularItemRenderer renderer;public net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer getCustomRenderer(){if(renderer==null)renderer=new MolecularItemRenderer();return renderer;}},AspItems.MOLECULAR_TRANSFORMER.get());}
    @SubscribeEvent static void colors(RegisterColorHandlersEvent.Item e){e.register((s,n)->n==0?FastColor.ARGB32.opaque(s.getOrDefault(DataComponents.DYED_COLOR,new DyedItemColor(0xffffff,false)).rgb()):-1,AspItems.HYBRID_SOLAR_HELMET.get(),AspItems.ULTIMATE_SOLAR_HELMET.get());}
    @SubscribeEvent static void renderers(EntityRenderersEvent.RegisterRenderers e){e.registerBlockEntityRenderer(AspBlockEntities.MOLECULAR_TRANSFORMER.get(),MolecularRenderer::new);}
}
