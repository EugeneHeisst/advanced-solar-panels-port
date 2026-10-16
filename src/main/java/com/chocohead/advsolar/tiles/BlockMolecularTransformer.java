package com.chocohead.advsolar.tiles;
import ic2.core.block.*;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.*;
public class BlockMolecularTransformer extends BlockTileEntity {
    public BlockMolecularTransformer(Properties p,Supplier<? extends BlockEntityType<? extends TileEntityBlock>> type){super(p,type);}
    protected RenderShape getRenderShape(BlockState s){return RenderShape.ENTITYBLOCK_ANIMATED;}
    protected VoxelShape getShape(BlockState s,BlockGetter l,BlockPos p,CollisionContext c){return Shapes.or(box(4,0,4,12,16,12),box(.8,0,3.2,9.6,16,12.8));}
    protected boolean hasAnalogOutputSignal(BlockState s){return true;}
    protected int getAnalogOutputSignal(BlockState s,net.minecraft.world.level.Level l,BlockPos p){return l.getBlockEntity(p) instanceof TileEntityMolecularAssembler m&&m.totalEU>0?(int)(15*m.energyUsed/m.totalEU):0;}
}
