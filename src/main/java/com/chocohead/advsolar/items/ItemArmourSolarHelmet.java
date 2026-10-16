package com.chocohead.advsolar.items;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.ref.AspArmorMaterials;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import ic2.api.item.*;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.util.Keyboard;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
public class ItemArmourSolarHelmet extends ItemArmorElectric {
    public enum Type {ADVANCED,HYBRID,ULTIMATE}
    public final Type type;
    public ItemArmourSolarHelmet(Type type){super(type==Type.ADVANCED?AspArmorMaterials.ADVANCED:type==Type.HYBRID?AspArmorMaterials.HYBRID:AspArmorMaterials.ULTIMATE,ArmorItem.Type.HELMET,new Item.Properties().rarity(type==Type.ADVANCED?Rarity.UNCOMMON:type==Type.HYBRID?Rarity.RARE:Rarity.EPIC),type==Type.ADVANCED?1000000:10000000,type==Type.ADVANCED?3000:10000,type==Type.ADVANCED?3:4);this.type=type;}
    public double getDamageAbsorptionRatio(){return type==Type.ADVANCED?.9:1;}public int getEnergyPerDamage(){return type==Type.ADVANCED?800:2000;}
    public AspConfig.Solar config(){return type==Type.ADVANCED?AspConfig.ADVANCED:type==Type.HYBRID?AspConfig.HYBRID:AspConfig.ULTIMATE;}
    public boolean canBeDyed(){return type!=Type.ADVANCED;}
    public int getHudMode(ItemStack stack){return Math.floorMod(stack.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag().getInt("hudMode"),4);}
    public void onArmorTick(Level l,Player p,ItemStack s){if(l.isClientSide)return;var tag=s.getOrDefault(DataComponents.CUSTOM_DATA,CustomData.EMPTY).copyTag();int timer=tag.getInt("toggleTimer");if(Keyboard.isAltKeyDown(p)&&Keyboard.isHudModeKeyDown(p)&&timer==0){int mode=(getHudMode(s)+1)%4;tag.putInt("hudMode",mode);timer=10;p.displayClientMessage(Component.translatable("advanced_solar_panels.hud.mode."+mode),false);}tag.putInt("toggleTimer",Math.max(0,timer-1));s.set(DataComponents.CUSTOM_DATA,CustomData.of(tag));
        if(type!=Type.ADVANCED&&p.getAirSupply()<100&&ElectricItem.manager.use(s,1000,null))p.setAirSupply(Math.min(p.getMaxAirSupply(),p.getAirSupply()+200));
        var sky=TileEntitySolarPanel.sky(l,p.blockPosition());double output=sky==TileEntitySolarPanel.GenerationState.DAY?config().day().get():sky==TileEntitySolarPanel.GenerationState.NIGHT?config().night().get():0;
        for(var slot:new EquipmentSlot[]{EquipmentSlot.FEET,EquipmentSlot.LEGS,EquipmentSlot.CHEST}){if(output<=0)break;output-=charge(p.getItemBySlot(slot),output);}
        if(!AspConfig.HELMET_ARMOUR_ONLY.get()){if(output>0)output-=charge(p.getOffhandItem(),output);for(var stack:p.getInventory().items){if(output<=0)break;if(stack!=s)output-=charge(stack,output);}}
        if(output>0)ElectricItem.manager.charge(s,output,Integer.MAX_VALUE,true,false);
    }
    private double charge(ItemStack s,double amount){return s.getItem() instanceof IElectricItem?ElectricItem.manager.charge(s,amount,tier,false,false):0;}
}
