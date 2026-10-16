package com.chocohead.advsolar.ref;

import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.items.ItemArmourSolarHelmet;
import com.chocohead.advsolar.items.ItemDoubleSlab;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AspItems {
    public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(AdvancedSolarPanels.MODID);

    public static final DeferredItem<BlockItem> MOLECULAR_TRANSFORMER = REGISTRY.registerSimpleBlockItem(AspBlocks.MOLECULAR_TRANSFORMER, new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> QUANTUM_GENERATOR = REGISTRY.registerSimpleBlockItem(AspBlocks.QUANTUM_GENERATOR, new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> ADVANCED_SOLAR_PANEL = REGISTRY.registerSimpleBlockItem(AspBlocks.ADVANCED_SOLAR_PANEL, new Item.Properties().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> HYBRID_SOLAR_PANEL = REGISTRY.registerSimpleBlockItem(AspBlocks.HYBRID_SOLAR_PANEL, new Item.Properties().rarity(Rarity.RARE));
    public static final DeferredItem<BlockItem> ULTIMATE_SOLAR_PANEL = REGISTRY.registerSimpleBlockItem(AspBlocks.ULTIMATE_SOLAR_PANEL, new Item.Properties().rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> QUANTUM_SOLAR_PANEL = REGISTRY.registerSimpleBlockItem(AspBlocks.QUANTUM_SOLAR_PANEL, new Item.Properties().rarity(Rarity.EPIC));

    // crafting items (the original's crafting@0..13)
    public static final DeferredItem<Item> SUNNARIUM = simple("sunnarium"), SUNNARIUM_PART = simple("sunnarium_part"), SUNNARIUM_ALLOY = simple("sunnarium_alloy"),
        IRRADIANT_URANIUM = simple("irradiant_uranium"), ENRICHED_SUNNARIUM = simple("enriched_sunnarium"), ENRICHED_SUNNARIUM_ALLOY = simple("enriched_sunnarium_alloy"),
        IRRADIANT_GLASS_PANE = simple("irradiant_glass_pane"), IRIDIUM_IRON_PLATE = simple("iridium_iron_plate"), REINFORCED_IRIDIUM_IRON_PLATE = simple("reinforced_iridium_iron_plate"),
        IRRADIANT_REINFORCED_PLATE = simple("irradiant_reinforced_plate"), IRIDIUM_INGOT = simple("iridium_ingot"), URANIUM_INGOT = simple("uranium_ingot"),
        MT_CORE = simple("mt_core"), QUANTUM_CORE = simple("quantum_core");

    public static final DeferredItem<ItemArmourSolarHelmet> ADVANCED_SOLAR_HELMET = REGISTRY.register("advanced_solar_helmet", () -> new ItemArmourSolarHelmet(ItemArmourSolarHelmet.Type.ADVANCED));
    public static final DeferredItem<ItemArmourSolarHelmet> HYBRID_SOLAR_HELMET = REGISTRY.register("hybrid_solar_helmet", () -> new ItemArmourSolarHelmet(ItemArmourSolarHelmet.Type.HYBRID));
    public static final DeferredItem<ItemArmourSolarHelmet> ULTIMATE_SOLAR_HELMET = REGISTRY.register("ultimate_solar_helmet", () -> new ItemArmourSolarHelmet(ItemArmourSolarHelmet.Type.ULTIMATE));
    public static final DeferredItem<ItemDoubleSlab> DOUBLE_STONE_SLAB = REGISTRY.register("double_stone_slab", () -> new ItemDoubleSlab(new Item.Properties()));

    private static DeferredItem<Item> simple(String name) { return REGISTRY.registerSimpleItem(name); }
    private AspItems() {}
}
