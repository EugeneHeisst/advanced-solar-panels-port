package com.chocohead.advsolar.ref;

import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.tiles.TileEntityMolecularAssembler;
import com.chocohead.advsolar.tiles.TileEntityQuantumGenerator;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AspBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedSolarPanels.MODID);

    public static final Supplier<BlockEntityType<TileEntityMolecularAssembler>> MOLECULAR_TRANSFORMER = reg("molecular_transformer", TileEntityMolecularAssembler::new, AspBlocks.MOLECULAR_TRANSFORMER);
    public static final Supplier<BlockEntityType<TileEntityQuantumGenerator>> QUANTUM_GENERATOR = reg("quantum_generator", TileEntityQuantumGenerator::new, AspBlocks.QUANTUM_GENERATOR);
    public static final Supplier<BlockEntityType<TileEntitySolarPanel>> ADVANCED_SOLAR_PANEL = reg("advanced_solar_panel", TileEntitySolarPanel::advanced, AspBlocks.ADVANCED_SOLAR_PANEL);
    public static final Supplier<BlockEntityType<TileEntitySolarPanel>> HYBRID_SOLAR_PANEL = reg("hybrid_solar_panel", TileEntitySolarPanel::hybrid, AspBlocks.HYBRID_SOLAR_PANEL);
    public static final Supplier<BlockEntityType<TileEntitySolarPanel>> ULTIMATE_SOLAR_PANEL = reg("ultimate_solar_panel", TileEntitySolarPanel::ultimate, AspBlocks.ULTIMATE_SOLAR_PANEL);
    public static final Supplier<BlockEntityType<TileEntitySolarPanel>> QUANTUM_SOLAR_PANEL = reg("quantum_solar_panel", TileEntitySolarPanel::quantum, AspBlocks.QUANTUM_SOLAR_PANEL);

    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> Supplier<BlockEntityType<T>> reg(String name, BlockEntityType.BlockEntitySupplier<T> factory, Supplier<? extends Block> block) {
        return REGISTRY.register(name, () -> BlockEntityType.Builder.of(factory, block.get()).build(null));
    }

    private AspBlockEntities() {}
}
