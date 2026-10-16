package com.chocohead.advsolar.gui;
import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.tiles.*;
import ic2.api.energy.EnergyNet;
import ic2.core.GuiIC2;
import ic2.core.network.ContainerEventPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;
public class GuiAsp extends GuiIC2<ContainerAsp<?>> {
    private final String kind,texture;
    public GuiAsp(ContainerAsp<?> c,Inventory i,Component title){super(c,i,title,c instanceof ContainerAsp.Solar?194:c instanceof ContainerAsp.Molecular?220:176,c instanceof ContainerAsp.Solar?168:193);kind=net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(c.base.getBlockState().getBlock()).getPath();texture=c instanceof ContainerAsp.Solar?"advancedsolarpanel":c instanceof ContainerAsp.Molecular?"moleculartransformer":"quantumgenerator";}
    public Component getName(){return Component.translatable("block.advanced_solar_panels."+kind);}public ResourceLocation getResourceLocation(){return AdvancedSolarPanels.id("textures/gui/"+texture+".png");}
    protected void init(){super.init();if(menu instanceof ContainerAsp.Quantum){int[] x={6,39,66,89,110,137},w={32,26,20,20,26,32};String[] labels={"-100","-10","-1","+1","+10","+100"};for(int n=0;n<6;n++){final int event=n;addRenderableWidget(Button.builder(Component.literal(labels[n]),b->send(event+(hasShiftDown()?10:0))).bounds(leftPos+x[n],topPos+40,w[n],20).build());addRenderableWidget(Button.builder(n==5?Component.translatable("advanced_solar_panels.gui.max"):Component.literal(""+(n+1)),b->send(20+event)).bounds(leftPos+6+n*26,topPos+84,n==5?34:24,20).build());}}}
    private void send(int event){PacketDistributor.sendToServer(new ContainerEventPayload(menu.containerId,""+event));}
    private void text(GuiGraphics g,String s,int x,int y,int max){float scale=Math.min(1,max/(float)Math.max(1,font.width(s)));g.pose().pushPose();g.pose().translate(x,y,0);g.pose().scale(scale,scale,1);g.drawString(font,s,0,0,0xffffff,false);g.pose().popPose();}
    private String tr(String key){return Component.translatable("advanced_solar_panels.gui."+key).getString();}
    protected void drawBackground(GuiGraphics g,float pt,int x,int y){if(menu instanceof ContainerAsp.Solar s){int w=(int)(24*s.base.storage/s.base.maxStorage);drawTexturedModalRect(g,leftPos+19,topPos+24,195,0,w,14);if(s.base.generation!=TileEntitySolarPanel.GenerationState.NONE)drawTexturedModalRect(g,leftPos+24,topPos+41,s.base.generation==TileEntitySolarPanel.GenerationState.DAY?195:210,15,14,14);}if(menu instanceof ContainerAsp.Quantum q&&q.base.getOfferedEnergy()>0)drawTexturedModalRect(g,leftPos+145,topPos+21,176,3,14,14);if(menu instanceof ContainerAsp.Molecular m&&m.base.totalEU>0)drawTexturedModalRect(g,leftPos+23,topPos+48,221,7,10,(int)(15*m.base.energyUsed/m.base.totalEU));}
    protected void drawForeground(GuiGraphics g,int x,int y){if(menu instanceof ContainerAsp.Solar s){text(g,tr("storage")+" "+(long)s.base.storage+" / "+s.base.maxStorage,50,22,137);text(g,tr("maxOutput")+" "+(long)EnergyNet.instance.getPowerFromTier(s.base.tier)+" EU/t",50,32,137);text(g,tr("generating")+" "+s.base.output()+" EU/t",50,42,137);}if(menu instanceof ContainerAsp.Quantum q){text(g,tr("output")+" "+q.base.production,12,25,126);text(g,tr("tier")+" "+(q.base.tier==6?tr("max"):q.base.tier),40,69,120);}if(menu instanceof ContainerAsp.Molecular m){String[] keys={"input","output","energyPerOperation","energyPerTick","progress"};String[] values={m.base.reserved.isEmpty()?"":m.base.reserved.getHoverName().getString(),m.base.result.isEmpty()?"":m.base.result.getHoverName().getString(),m.base.totalEU+" EU",(long)m.base.lastEnergyGiven+" EU/t",m.base.totalEU==0?"0%":String.format(java.util.Locale.ROOT,"%.1f%%",100*m.base.energyUsed/m.base.totalEU)};for(int n=0;n<5;n++){text(g,tr(keys[n]),46,26+12*n,61);text(g,values[n],112,26+12*n,101);}}}
}
