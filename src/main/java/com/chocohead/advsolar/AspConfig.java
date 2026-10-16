package com.chocohead.advsolar;

import net.neoforged.neoforge.common.ModConfigSpec;

/** advanced_solar_panels-common.toml: the 4.3.0 config values (solars, quantum generator, helmets, recipe switches). */
public final class AspConfig {
    public static final ModConfigSpec SPEC;
    public static final java.util.Map<String,ModConfigSpec.BooleanValue> RECIPE_FLAGS=new java.util.LinkedHashMap<>();
    public static final Solar ADVANCED, HYBRID, ULTIMATE, QUANTUM;
    public static final ModConfigSpec.IntValue QG_PRODUCTION, QG_TIER;
    public static final ModConfigSpec.BooleanValue HELMET_ARMOUR_ONLY, DRAW_ACTIVE_CORE;

    public record Solar(ModConfigSpec.IntValue day, ModConfigSpec.IntValue night, ModConfigSpec.IntValue storage, ModConfigSpec.IntValue tier) {}

    static {
        var b = new ModConfigSpec.Builder();
        b.push("solars");
        ADVANCED = solar(b, "AdvancedSP", 8, 1, 32000, 1);
        HYBRID = solar(b, "HybrydSP", 64, 8, 100000, 2);
        ULTIMATE = solar(b, "UltimateHSP", 512, 64, 1000000, 3);
        QUANTUM = solar(b, "QuantumSP", 4096, 2048, 10000000, 5);
        b.pop().push("quantum_generator");
        QG_PRODUCTION = b.defineInRange("quantumGeneratorDefaultProduction", 512, 0, Integer.MAX_VALUE);
        QG_TIER = b.defineInRange("quantumGeneratorDefaultTier", 3, 1, 6);
        b.pop().push("general");
        HELMET_ARMOUR_ONLY = b.comment("Should solar helmets charge armour only? (false also charges the hotbar, offhand and inventory)").define("helmetsChargeArmourOnly", true);
        DRAW_ACTIVE_CORE = b.comment("Draw the Molecular Transformer active animation").define("drawActiveCore", false);
        b.pop();
        b.push("recipes");
        RECIPE_FLAGS.put("hard",b.define("hard",true));RECIPE_FLAGS.put("simple_advanced",b.define("simple_advanced",false));
        for(String name:java.util.List.of("advanced_solar_panel","hybrid_solar_panel","ultimate_solar_panel","quantum_solar_panel","advanced_solar_helmet","hybrid_solar_helmet","ultimate_solar_helmet","molecular_transformer","double_stone_slab"))RECIPE_FLAGS.put(name,b.define("enable_"+name,true));
        b.pop();SPEC = b.build();
    }

    private static Solar solar(ModConfigSpec.Builder b, String prefix, int day, int night, int storage, int tier) {
        return new Solar(b.defineInRange(prefix + "GenDay", day, 0, Integer.MAX_VALUE), b.defineInRange(prefix + "GenNight", night, 0, Integer.MAX_VALUE),
            b.defineInRange(prefix + "Storage", storage, 1, Integer.MAX_VALUE), b.defineInRange(prefix + "Tier", tier, 1, 13));
    }

    private AspConfig() {}
}
