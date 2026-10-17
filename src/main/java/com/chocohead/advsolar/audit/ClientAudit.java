package com.chocohead.advsolar.audit;
import com.chocohead.advsolar.*;
import com.chocohead.advsolar.ref.*;
import com.chocohead.advsolar.tiles.*;
import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import ic2.core.IHasGui;
import ic2.core.ref.Ic2Items;
import net.minecraft.client.*;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.nio.file.*;
import java.util.*;
@EventBusSubscriber(modid=AdvancedSolarPanels.MODID,value=Dist.CLIENT)
public class ClientAudit {
    private static final boolean ENABLED="client".equals(System.getProperty("asp.audit"));private static int tick;private static final List<BlockPos> machines=new ArrayList<>();
    private static void shot(Minecraft mc,String name){try{Files.createDirectories(Path.of("audit"));try(var image=Screenshot.takeScreenshot(mc.getMainRenderTarget())){image.writeToFile(Path.of("audit",name+".png"));}}catch(Exception e){throw new RuntimeException(e);}}
    @SubscribeEvent static void tick(ClientTickEvent.Post event){if(!ENABLED)return;var mc=Minecraft.getInstance();if(mc.player==null||mc.getSingleplayerServer()==null)return;tick++;var server=mc.getSingleplayerServer();if(tick%100==0)AdvancedSolarPanels.LOG.info("[asp-client-audit] tick {} screen {}",tick,mc.screen);
        if(tick==30)server.execute(()->{var l=server.overworld();var p=server.getPlayerList().getPlayers().getFirst();p.setGameMode(net.minecraft.world.level.GameType.CREATIVE);p.getAbilities().flying=true;p.onUpdateAbilities();AspConfig.DRAW_ACTIVE_CORE.set(true);l.setDayTime(6000);l.setWeatherParameters(6000,0,false,false);for(var pos:BlockPos.betweenClosed(new BlockPos(-4,119,-5),new BlockPos(22,119,5)))l.setBlock(pos,Blocks.STONE.defaultBlockState(),3);for(var pos:BlockPos.betweenClosed(new BlockPos(-4,120,-4),new BlockPos(22,124,4)))l.setBlock(pos,Blocks.AIR.defaultBlockState(),3);l.getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,new net.minecraft.world.phys.AABB(-8,118,-8,26,130,8)).forEach(net.minecraft.world.entity.Entity::discard);int n=0;
            for(var block:AspBlocks.REGISTRY.getEntries()){var pos=new BlockPos(n++*3,120,0);l.setBlock(pos,block.get().defaultBlockState(),3);machines.add(pos);var be=l.getBlockEntity(pos);if(be instanceof TileEntitySolarPanel s){s.storage=s.maxStorage/2.;for(int i=0;i<4;i++){var battery=new ItemStack(Ic2Items.RE_BATTERY.get());ic2.api.item.ElectricItem.manager.charge(battery,Double.MAX_VALUE,4,true,false);s.chargeSlots.put(i,battery);}}if(be instanceof TileEntityMolecularAssembler m){m.inputSlot.put(new ItemStack(Items.YELLOW_WOOL,4));m.work();m.injectEnergy(Direction.NORTH,250000,32);l.setBlock(pos.south(),AspBlocks.QUANTUM_GENERATOR.get().defaultBlockState(),3);}}
            p.teleportTo(l,7.5,122,-9,0,12);p.getInventory().clearContent();p.getInventory().selected=8;int i=0;for(var item:AspItems.REGISTRY.getEntries())p.getInventory().setItem(i++,new ItemStack(item.get()));
        });
        if(tick==100)shot(mc,"machines");
        if(tick>=110&&tick<110+machines.size()*25){int n=(tick-110)/25;var pos=machines.get(n);if((tick-110)%25==0)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.teleportTo(server.overworld(),pos.getX()+.5,pos.getY()+1,pos.getZ()-3,0,20);var gui=(IHasGui)server.overworld().getBlockEntity(pos);p.openMenu(new SimpleMenuProvider((id,i,pl)->gui.getGuiContainer(id,i),Component.empty()),buf->buf.writeBlockPos(pos));});if((tick-110)%25==18)shot(mc,"gui_"+net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(server.overworld().getBlockState(pos).getBlock()).getPath());if((tick-110)%25==22)mc.player.closeContainer();}
        int end=110+machines.size()*25;
        if(tick==end+5)server.execute(()->{var p=server.getPlayerList().getPlayers().getFirst();p.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);p.getAbilities().mayfly=true;p.getAbilities().flying=true;p.onUpdateAbilities();var helmet=AspItems.HYBRID_SOLAR_HELMET.get().charged();var hud=new net.minecraft.nbt.CompoundTag();hud.putInt("hudMode",3);helmet.set(DataComponents.CUSTOM_DATA,net.minecraft.world.item.component.CustomData.of(hud));helmet.set(DataComponents.DYED_COLOR,new DyedItemColor(0xff3030,true));p.setItemSlot(EquipmentSlot.HEAD,helmet);});
        if(tick==end+15)mc.setScreen(new InventoryScreen(mc.player));if(tick==end+30)shot(mc,"solar_helmets");if(tick==end+35)mc.player.closeContainer();if(tick==end+40)shot(mc,"solar_hud");if(tick==end+42&&net.neoforged.fml.ModList.get().isLoaded("jei")){boolean shown=com.chocohead.advsolar.jei.AspJeiPlugin.showForAudit();AdvancedSolarPanels.LOG.info("[asp-client-audit] JEI molecular page {}",shown);if(!shown)throw new IllegalStateException("ASP JEI page not registered");}
        if(tick==end+55&&net.neoforged.fml.ModList.get().isLoaded("jei"))shot(mc,"jei_molecular");if(tick==end+60&&net.neoforged.fml.ModList.get().isLoaded("jei")){boolean shown=ic2.core.jei.IC2JeiPlugin.showForAudit("macerator");AdvancedSolarPanels.LOG.info("[asp-client-audit] IC2 JEI macerator page {}",shown);if(!shown)throw new IllegalStateException("IC2 JEI page not registered");}if(tick==end+75&&net.neoforged.fml.ModList.get().isLoaded("jei"))shot(mc,"jei_ic2_macerator");
        if(tick==end+80)HelmetIconAudit.open(mc);if(tick==end+110){HelmetIconAudit.finish();AdvancedSolarPanels.LOG.info("[asp-client-audit] DONE");}if(tick>=end+115)mc.stop();
    }
}
