package com.chocohead.advsolar.ref;

import com.chocohead.advsolar.AdvancedSolarPanels;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import java.util.function.Supplier;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The six machines of the original TEs enum (hardness 3, resistance 15, pickaxe, drop themselves). */
public final class AspBlocks {
    public static final DeferredRegister.Blocks REGISTRY = DeferredRegister.createBlocks(AdvancedSolarPanels.MODID);

    private static BlockBehaviour.Properties machine() { return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3, 15).requiresCorrectToolForDrops().sound(SoundType.METAL); }
    private static DeferredBlock<BlockTileEntity> reg(String name, Supplier<BlockEntityType<? extends TileEntityBlock>> type) { return REGISTRY.register(name, () -> new BlockTileEntity(machine(), type)); }

    public static final DeferredBlock<com.chocohead.advsolar.tiles.BlockMolecularTransformer> MOLECULAR_TRANSFORMER = REGISTRY.register("molecular_transformer",
        () -> new com.chocohead.advsolar.tiles.BlockMolecularTransformer(machine().noOcclusion().lightLevel(s -> s.getValue(BlockTileEntity.ACTIVE) ? 12 : 0), () -> AspBlockEntities.MOLECULAR_TRANSFORMER.get()));
    public static final DeferredBlock<BlockTileEntity> QUANTUM_GENERATOR = reg("quantum_generator", () -> AspBlockEntities.QUANTUM_GENERATOR.get());
    public static final DeferredBlock<BlockTileEntity> ADVANCED_SOLAR_PANEL = reg("advanced_solar_panel", () -> AspBlockEntities.ADVANCED_SOLAR_PANEL.get());
    public static final DeferredBlock<BlockTileEntity> HYBRID_SOLAR_PANEL = reg("hybrid_solar_panel", () -> AspBlockEntities.HYBRID_SOLAR_PANEL.get());
    public static final DeferredBlock<BlockTileEntity> ULTIMATE_SOLAR_PANEL = reg("ultimate_solar_panel", () -> AspBlockEntities.ULTIMATE_SOLAR_PANEL.get());
    public static final DeferredBlock<BlockTileEntity> QUANTUM_SOLAR_PANEL = reg("quantum_solar_panel", () -> AspBlockEntities.QUANTUM_SOLAR_PANEL.get());

    private AspBlocks() {}
}
