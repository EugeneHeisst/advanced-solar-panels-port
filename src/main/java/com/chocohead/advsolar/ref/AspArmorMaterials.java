package com.chocohead.advsolar.ref;
import com.chocohead.advsolar.AdvancedSolarPanels;
import java.util.*;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;
public class AspArmorMaterials {
    public static final DeferredRegister<ArmorMaterial> REGISTRY=DeferredRegister.create(Registries.ARMOR_MATERIAL,AdvancedSolarPanels.MODID);
    public static final Holder<ArmorMaterial> ADVANCED=reg("advanced_solar",false),HYBRID=reg("hybrid_solar",true),ULTIMATE=reg("ultimate_solar",true);
    private static Holder<ArmorMaterial> reg(String name,boolean dye){return REGISTRY.register(name,()->{var defense=new EnumMap<ArmorItem.Type,Integer>(ArmorItem.Type.class);for(var t:ArmorItem.Type.values())defense.put(t,0);var layers=new ArrayList<ArmorMaterial.Layer>();layers.add(new ArmorMaterial.Layer(AdvancedSolarPanels.id(name),"",dye));if(dye)layers.add(new ArmorMaterial.Layer(AdvancedSolarPanels.id(name),"_overlay",false));return new ArmorMaterial(defense,0,SoundEvents.ARMOR_EQUIP_DIAMOND,()->Ingredient.EMPTY,layers,0,0);});}
}
