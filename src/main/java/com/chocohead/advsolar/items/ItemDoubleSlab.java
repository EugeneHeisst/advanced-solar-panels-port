package com.chocohead.advsolar.items;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
public class ItemDoubleSlab extends BlockItem {
 @Override public String getDescriptionId(){return "item.advanced_solar_panels.double_stone_slab";}
    public ItemDoubleSlab(Properties p){super(Blocks.SMOOTH_STONE_SLAB,p);}
    // This is an alternate placement item, so the vanilla slab must keep its normal item association.
    @Override public void registerBlocks(java.util.Map<net.minecraft.world.level.block.Block,Item> map,Item item){}
    @Override protected BlockState getPlacementState(BlockPlaceContext c){return Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(BlockStateProperties.SLAB_TYPE,SlabType.DOUBLE);}
}
