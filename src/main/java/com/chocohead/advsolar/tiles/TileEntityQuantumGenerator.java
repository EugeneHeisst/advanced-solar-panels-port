package com.chocohead.advsolar.tiles;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.ref.*;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySource;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class TileEntityQuantumGenerator extends TileEntityInventory implements IEnergySource,IHasGui {
    public int production=AspConfig.QG_PRODUCTION.get(),tier=AspConfig.QG_TIER.get();private boolean registered;
    public TileEntityQuantumGenerator(BlockPos p,BlockState s){super(AspBlockEntities.QUANTUM_GENERATOR.get(),p,s);}
    protected void updateEntityServer(){setActive(!level.hasNeighborSignal(worldPosition));}
    public void event(int event){if(event>=0&&event<=5||event>=10&&event<=15){int[] amounts={-100,-10,-1,1,10,100};production=(int)Math.max(0,Math.min(Integer.MAX_VALUE,(long)production+amounts[event%10]*(event>=10?5L:1L)));}else if(event>=20&&event<=25){if(registered)EnergyNet.instance.removeTile(this);tier=event-19;if(registered)EnergyNet.instance.addTile(this);}else return;setChanged();}
    public boolean emitsEnergyTo(BlockEntity r,Direction d){return true;}public int getSourceTier(){return tier;}public double getOfferedEnergy(){return level!=null&&!level.hasNeighborSignal(worldPosition)?production:0;}public void drawEnergy(double n){}
    public void onLoaded(){super.onLoaded();if(!level.isClientSide&&!registered){EnergyNet.instance.addTile(this);registered=true;}updateEntityServer();}
    public void onUnloaded(){if(registered){EnergyNet.instance.removeTile(this);registered=false;}super.onUnloaded();}
    public ContainerBase<?> getGuiContainer(int id,Inventory i){return new com.chocohead.advsolar.gui.ContainerAsp.Quantum(id,i,this);}
    public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);production=Math.max(0,n.getInt("production"));tier=Math.max(1,Math.min(6,n.getInt("tier")));}
    public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);n.putInt("production",production);n.putInt("tier",tier);}
}
