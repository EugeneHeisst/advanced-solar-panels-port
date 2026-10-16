package com.chocohead.advsolar.tiles;
import com.chocohead.advsolar.ref.*;
import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.core.*;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.*;
import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
public class TileEntityMolecularAssembler extends TileEntityInventory implements IEnergySink,IHasGui {
    public final InvSlot inputSlot=new InvSlot(this,"input",InvSlot.Access.I,1);
    public final InvSlotOutput outputSlot=new InvSlotOutput(this,"output",1);
    public ItemStack reserved=ItemStack.EMPTY,result=ItemStack.EMPTY;public double energyUsed,lastEnergyGiven,received;public long totalEU;public int idle;private boolean registered;
    public TileEntityMolecularAssembler(BlockPos p,BlockState s){super(AspBlockEntities.MOLECULAR_TRANSFORMER.get(),p,s);}
    public boolean work(){if(reserved.isEmpty()){
        var recipe=level.getRecipeManager().getRecipeFor(AspRecipes.MOLECULAR.get(),new SingleRecipeInput(inputSlot.get()),level);if(recipe.isEmpty())return false;var r=recipe.get().value();var out=r.result(level.registryAccess());if(!outputSlot.canAdd(out))return false;
        reserved=inputSlot.get().copyWithCount(r.count());inputSlot.get().shrink(r.count());result=out;totalEU=r.energy();energyUsed=0;setChanged();return true;
    }if(!outputSlot.canAdd(result))return false;if(energyUsed>=totalEU){outputSlot.add(result.copy());reserved=result=ItemStack.EMPTY;totalEU=0;energyUsed=0;setChanged();return true;}return false;}
    protected void updateEntityServer(){lastEnergyGiven=received;received=0;boolean changed=work();if(changed||lastEnergyGiven>0)idle=0;else idle=Math.min(40,idle+1);setActive(!reserved.isEmpty()&&idle<40);if(changed||lastEnergyGiven>0){setChanged();level.updateNeighbourForOutputSignal(worldPosition,getBlockState().getBlock());}}
    public double getDemandedEnergy(){return reserved.isEmpty()||!outputSlot.canAdd(result)?0:Math.max(0,totalEU-energyUsed);}
    public double injectEnergy(Direction d,double amount,double voltage){if(!Double.isFinite(amount)||amount<=0)return amount;double accepted=Math.min(amount,getDemandedEnergy());energyUsed+=accepted;received+=accepted;if(accepted>0)setChanged();return amount-accepted;}
    public int getSinkTier(){return 14;}public boolean acceptsEnergyFrom(BlockEntity e,Direction d){return true;}
    public void onLoaded(){super.onLoaded();if(!level.isClientSide&&!registered){EnergyNet.instance.addTile(this);registered=true;}}
    public void onUnloaded(){if(registered){EnergyNet.instance.removeTile(this);registered=false;}super.onUnloaded();}
    public void onBlockBreak(){if(!reserved.isEmpty()){Block.popResource(level,worldPosition,reserved.copy());reserved=ItemStack.EMPTY;}}
    public ContainerBase<?> getGuiContainer(int id,Inventory i){return new com.chocohead.advsolar.gui.ContainerAsp.Molecular(id,i,this);}
    public void readFromNBT(CompoundTag n,HolderLookup.Provider r){super.readFromNBT(n,r);reserved=ItemStack.parseOptional(r,n.getCompound("reserved"));result=ItemStack.parseOptional(r,n.getCompound("result"));totalEU=Math.max(0,n.getLong("totalEU"));energyUsed=Math.max(0,Math.min(totalEU,n.getDouble("energyUsed")));if(reserved.isEmpty()||result.isEmpty()||totalEU==0){reserved=result=ItemStack.EMPTY;totalEU=0;energyUsed=0;}}
    public void writeToNBT(CompoundTag n,HolderLookup.Provider r){super.writeToNBT(n,r);if(!reserved.isEmpty())n.put("reserved",reserved.save(r));if(!result.isEmpty())n.put("result",result.save(r));n.putLong("totalEU",totalEU);n.putDouble("energyUsed",energyUsed);}
}
