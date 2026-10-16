package com.chocohead.advsolar.items;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
public class ItemDoubleSlab extends BlockItem {
    public ItemDoubleSlab(Properties p){super(Blocks.SMOOTH_STONE_SLAB,p);}
    @Override protected BlockState getPlacementState(BlockPlaceContext c){return Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE,SlabType.DOUBLE);}
}
