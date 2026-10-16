# Advanced Solar Panels PORT — handoff (Claude ⇄ Codex)

Rule from the user: when Claude's limit runs out, Codex continues from here, and returns the task to Claude when Claude's limit resets. To return it, write a final "Codex → Claude resume point" section and stop starting new batches. No push/release/install without the user; the GitHub repo is private.

Reference: related/asp-reference (ASP 4.3.0 by Chocohead, MC 1.12.2, decompiled sources in src/, assets in jar/). Depends on IC2 PORT 0.1.17 (../industrialcraft2-modern/versions/0.1.17/…jar, set by `ic2_version` in gradle.properties). IC2 PORT is complete through 0.1.17 (see its CODEX_HANDOFF.md).

## Claude batch 0.1.0 — in progress (2026-10-16 ~19:20)

Done so far (not yet compiled):
- build.gradle (IC2 jar as implementation, JEI compileOnly, runs client/server/audit);
- neoforge.mods.toml (requires ic2 ≥ ic2_version);
- AspConfig (common config with the 4.3.0 values);
- AdvancedSolarPanels (@Mod, sided item capability);
- MachineRecipes (compressor/extruder iridium and uranium ingots through the IC2 API);
- ref/AspBlocks (six machines on ic2 BlockTileEntity);
- ref/AspBlockEntities;
- ref/AspMenus (own menu type built from IHasGui).

Plan (package com.chocohead.advsolar):
- tiles/TileEntitySolarPanel: one class for the four panels, with static factories advanced/hybrid/ultimate/quantum. IEnergySource, 4 charge slots (slots/InvSlotMultiCharge), sky check every 128 ticks, day/night/none, storage/tier from AspConfig.
- tiles/TileEntityQuantumGenerator: IEnergySource offering `production` at `tier` while there is no redstone. drawEnergy is a no-op. GUI buttons with events 0–5 / 10–15 (shift) / 20–25 for the tier. The IC2 port grid has no packet API, so offering the whole production is fine.
- tiles/TileEntityMolecularAssembler + BlockMolecularTransformer (noOcclusion, light 12 when active, ENTITYBLOCK_ANIMATED), IEnergySink with sink tier 14. Recipes are a data-driven recipe type `advanced_solar_panels:molecular_transformer` (ingredient + count, result item or tag, energy) holding the 22 default recipes from Configs.fillDefault.
- GUIs:
  - solar panels on gui/advancedsolarpanel.png (194x168, energy gauge u195 v0 24x14 right, sun icon u195 v15 / moon u210 v15);
  - quantum generator on quantumgenerator.png (176x193);
  - molecular transformer on moleculartransformer.png (220x193, progress u221 v7 10x15 down).
- items:
  - 14 crafting items (sunnarium … quantum_core);
  - 3 solar helmets (extend ic2 ItemArmorElectric; hybrid/ultimate dyeable with an overlay);
  - double_stone_slab item.
- Tags: c:ingots/uranium, c:ingots/iridium.
- Crafting recipes from Recipes.java use ic2:shaped/shapeless so charge carries over. IC2 port ids: advanced_alloy, carbon_plate, advanced_circuit, iridium_plate, coal_chunk, solar_panel, advanced_machine, reinforced_glass, nano_helmet, quantum_helmet, insulated_gold_cable, glass_fibre_cable, lv/hv/ev_transformer, thick_neutron_reflector, iridium_ore, industrial_diamond.
- Assets: rename the jar textures (spaces) to snake_case.
- Remaining work: lang en/ru from jar/.../lang, the MT renderer (PrettyMolecularTransformerModel port), JEI MT category, a server audit and a hidden silent client audit like IC2's, then versions/0.1.0 artifacts and a commit to the private repo.
