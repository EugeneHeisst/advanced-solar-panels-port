package com.chocohead.advsolar.client;
import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import ic2.api.item.ElectricItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
@EventBusSubscriber(modid=AdvancedSolarPanels.MODID,value=Dist.CLIENT)
public class SolarHud {
    @SubscribeEvent static void draw(RenderGuiEvent.Post e){var mc=Minecraft.getInstance();if(mc.player==null||mc.options.hideGui||mc.screen!=null)return;var s=mc.player.getItemBySlot(EquipmentSlot.HEAD);if(!(s.getItem() instanceof ItemArmourSolarHelmet helmet)||ElectricItem.manager.getCharge(s)<=0||helmet.getHudMode(s)==0)return;int mode=helmet.getHudMode(s),y=5;for(var slot:new EquipmentSlot[]{EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}){var item=mc.player.getItemBySlot(slot);String tip=ElectricItem.manager.getToolTip(item);if(tip!=null){e.getGuiGraphics().drawString(mc.font,(mode>1?item.getHoverName().getString()+": ":"")+tip,5,y,0xffc0ffff);y+=11;}}if(mode==3)for(var item:mc.player.getInventory().items.subList(0,9)){String tip=ElectricItem.manager.getToolTip(item);if(tip!=null){e.getGuiGraphics().drawString(mc.font,item.getHoverName().getString()+": "+tip,5,y,0xffc0ffff);y+=11;}}}
}
