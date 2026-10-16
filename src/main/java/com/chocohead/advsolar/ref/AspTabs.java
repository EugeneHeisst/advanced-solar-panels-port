package com.chocohead.advsolar.ref;

import com.chocohead.advsolar.AdvancedSolarPanels;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AspTabs {
    public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedSolarPanels.MODID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = REGISTRY.register("main", () -> CreativeModeTab.builder()
        .title(Component.translatable("itemGroup.advanced_solar_panels")).icon(() -> new ItemStack(AspItems.QUANTUM_SOLAR_PANEL.get()))
        .displayItems((params, out) -> {
            for (var item : AspItems.REGISTRY.getEntries()) {
                if (item.get() instanceof com.chocohead.advsolar.items.ItemArmourSolarHelmet helmet) { out.accept(new ItemStack(helmet)); out.accept(helmet.charged()); }
                else out.accept(item.get());
            }
        }).build());
    private AspTabs() {}
}
