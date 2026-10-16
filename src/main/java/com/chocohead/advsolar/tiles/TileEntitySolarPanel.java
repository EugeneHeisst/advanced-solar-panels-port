package com.chocohead.advsolar.tiles;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.ref.*;
import com.chocohead.advsolar.slots.InvSlotMultiCharge;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.*;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
public class TileEntitySolarPanel extends TileEntityInventory implements IEnergySource,IHasGui {
    public enum GenerationState {NONE,NIGHT,DAY}
    public int maxStorage,dayPower,nightPower,tier;
    public final InvSlotMultiCharge chargeSlots;
    public double storage;public GenerationState generation=GenerationState.NONE;public int ticker;
    private boolean registered;
    public TileEntitySolarPanel(BlockEntityType<?> type,BlockPos p,BlockState s,AspConfig.Solar config){super(type,p,s);maxStorage=config.storage().get();dayPower=config.day().get();nightPower=config.night().get();tier=config.tier().get();chargeSlots=new InvSlotMultiCharge(this,tier,4,InvSlot.Access.IO);}
    public static TileEntitySolarPanel advanced(BlockPos p,BlockState s){return new TileEntitySolarPanel(AspBlockEntities.ADVANCED_SOLAR_PANEL.get(),p,s,AspConfig.ADVANCED);}
    public static TileEntitySolarPanel hybrid(BlockPos p,BlockState s){return new TileEntitySolarPanel(AspBlockEntities.HYBRID_SOLAR_PANEL.get(),p,s,AspConfig.HYBRID);}
    public static TileEntitySolarPanel ultimate(BlockPos p,BlockState s){return new TileEntitySolarPanel(AspBlockEntities.ULTIMATE_SOLAR_PANEL.get(),p,s,AspConfig.ULTIMATE);}
    public static TileEntitySolarPanel quantum(BlockPos p,BlockState s){return new TileEntitySolarPanel(AspBlockEntities.QUANTUM_SOLAR_PANEL.get(),p,s,AspConfig.QUANTUM);}
    public static GenerationState sky(Level l,BlockPos p){if(!l.dimensionType().hasSkyLight()||l.getChunkAt(p).getSkyLightSources().getLowestSourceY(p.getX()&15,p.getZ()&15)>p.getY())return GenerationState.NONE;return !l.isDay()||l.getBiome(p).value().hasPrecipitation()&&(l.isRaining()||l.isThundering())?GenerationState.NIGHT:GenerationState.DAY;}
    public void checkTheSky(){generation=sky(level,worldPosition.above());}
    public void tryGenerateEnergy(int amount){storage=Math.min(maxStorage,storage+Math.max(0,amount));}
    public int output(){return generation==GenerationState.DAY?dayPower:generation==GenerationState.NIGHT?nightPower:0;}
    protected void updateEntityServer(){if(ticker++%128==0)checkTheSky();double old=storage;tryGenerateEnergy(output());storage-=chargeSlots.charge(storage);setActive(generation!=GenerationState.NONE);if(storage!=old)setChanged();}
    public boolean emitsEnergyTo(BlockEntity receiver,Direction side){return true;}public int getSourceTier(){return tier;}public double getOfferedEnergy(){return Math.min(storage,EnergyNet.instance.getPowerFromTier(tier));}public void drawEnergy(double n){storage=Math.max(0,storage-Math.max(0,n));setChanged();}
    public void onLoaded(){super.onLoaded();if(!level.isClientSide&&!registered){EnergyNet.instance.addTile(this);registered=true;checkTheSky();}}
    public void onUnloaded(){if(registered){EnergyNet.instance.removeTile(this);registered=false;}super.onUnloaded();}
    public ContainerBase<?> getGuiContainer(int id,Inventory i){return new com.chocohead.advsolar.gui.ContainerAsp.Solar(id,i,this);}
    public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);storage=Math.max(0,Math.min(maxStorage,n.getDouble("storage")));}
    public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);n.putDouble("storage",storage);}
}
