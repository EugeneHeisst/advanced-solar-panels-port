package com.chocohead.advsolar.ref;

import com.chocohead.advsolar.AdvancedSolarPanels;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

/** GUIs of the ASP machines: built on both sides from the block entity at the sent position, like IC2's ic2:tile menu. */
public final class AspMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, AdvancedSolarPanels.MODID);
    public static final Supplier<MenuType<ContainerBase<?>>> TILE = REGISTRY.register("tile", () -> IMenuTypeExtension.create((id, inv, buf) -> {
        if (inv.player.level().getBlockEntity(buf.readBlockPos()) instanceof IHasGui gui) return gui.getGuiContainer(id, inv, buf);
        throw new IllegalStateException("No ASP GUI block entity at the sent position");
    }));
    private AspMenus() {}
}
