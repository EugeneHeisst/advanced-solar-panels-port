package com.chocohead.advsolar.gui;
import com.chocohead.advsolar.ref.AspMenus;
import com.chocohead.advsolar.tiles.*;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.slot.SlotInvSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
public abstract class ContainerAsp<T extends TileEntityInventory> extends ContainerBase<T>{
    protected ContainerAsp(int id,Inventory i,T b,int width,int height){super(AspMenus.TILE.get(),id,i,b);if(width==220){for(int row=0;row<3;row++)for(int col=0;col<9;col++)addSlot(new net.minecraft.world.inventory.Slot(i,col+row*9+9,18+col*21,98+row*21));for(int col=0;col<9;col++)addSlot(new net.minecraft.world.inventory.Slot(i,col,18+col*21,165));}else addPlayerInventorySlots(i,width,height);}
    public static class Solar extends ContainerAsp<TileEntitySolarPanel>{
        public Solar(int id,Inventory i,TileEntitySolarPanel b){super(id,i,b,194,168);for(int n=0;n<4;n++)addSlot(new SlotInvSlot(b.chargeSlots,n,17+n*18,59));sync(()->b.maxStorage,v->b.maxStorage=(int)v);sync(()->b.dayPower,v->b.dayPower=(int)v);sync(()->b.nightPower,v->b.nightPower=(int)v);sync(()->b.tier,v->b.tier=(int)v);sync(()->b.storage,v->b.storage=v);sync(()->b.generation.ordinal(),v->b.generation=TileEntitySolarPanel.GenerationState.values()[Math.max(0,Math.min(2,(int)v))]);}
    }
    public static class Quantum extends ContainerAsp<TileEntityQuantumGenerator>{
        public Quantum(int id,Inventory i,TileEntityQuantumGenerator b){super(id,i,b,176,193);sync(()->b.production,v->b.production=(int)v);sync(()->b.tier,v->b.tier=(int)v);}
        public void onContainerEvent(String s){try{base.event(Integer.parseInt(s));}catch(NumberFormatException ignored){}}
    }
    public static class Molecular extends ContainerAsp<TileEntityMolecularAssembler>{
        private int inputId,outputId;
        public Molecular(int id,Inventory i,TileEntityMolecularAssembler b){super(id,i,b,220,193);addSlot(new SlotInvSlot(b.inputSlot,0,20,27));addSlot(new SlotInvSlot(b.outputSlot,0,20,68));sync(()->b.energyUsed,v->b.energyUsed=v);sync(()->b.totalEU,v->b.totalEU=(long)v);sync(()->b.lastEnergyGiven,v->b.lastEnergyGiven=v);sync(()->BuiltInRegistries.ITEM.getId(b.reserved.getItem()),v->{inputId=(int)v;b.reserved=inputId==0?ItemStack.EMPTY:new ItemStack(BuiltInRegistries.ITEM.byId(inputId));});sync(()->BuiltInRegistries.ITEM.getId(b.result.getItem()),v->{outputId=(int)v;b.result=outputId==0?ItemStack.EMPTY:new ItemStack(BuiltInRegistries.ITEM.byId(outputId));});}
    }
}
