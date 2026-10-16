package com.chocohead.advsolar;

import com.chocohead.advsolar.ref.AspBlockEntities;
import com.chocohead.advsolar.ref.AspBlocks;
import com.chocohead.advsolar.ref.AspItems;
import com.chocohead.advsolar.ref.AspMenus;
import com.chocohead.advsolar.ref.AspRecipes;
import com.chocohead.advsolar.ref.AspTabs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Advanced Solar Panels 4.3.0 (Icedfire, SeNtiMeL, Chocohead) ported to 1.21.1 as an addon of the IndustrialCraft 2 PORT. */
@Mod(AdvancedSolarPanels.MODID)
public final class AdvancedSolarPanels {
    public static final String MODID = "advanced_solar_panels";
    public static final Logger LOG = LoggerFactory.getLogger("AdvancedSolarPanels");

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath(MODID, path); }

    public AdvancedSolarPanels(IEventBus bus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, AspConfig.SPEC);
        AspBlocks.REGISTRY.register(bus);
        AspItems.REGISTRY.register(bus);
        AspBlockEntities.REGISTRY.register(bus);
        AspMenus.REGISTRY.register(bus);
        AspRecipes.TYPES.register(bus);
        AspRecipes.SERIALIZERS.register(bus);
        AspTabs.REGISTRY.register(bus);
        bus.addListener(this::setup);
        bus.addListener(this::capabilities);
    }

    private void setup(FMLCommonSetupEvent event) { event.enqueueWork(MachineRecipes::add); }

    private void capabilities(RegisterCapabilitiesEvent event) {
        for (var type : AspBlockEntities.REGISTRY.getEntries())
            event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, type.get(), (be, side) -> be instanceof ic2.core.block.TileEntityInventory inv && side != null ? new SidedInvWrapper(inv, side) : null);
    }
}
