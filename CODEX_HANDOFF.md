# Advanced Solar Panels PORT — handoff (Claude ⇄ Codex)

Rule from the user: when Claude's limit runs out, Codex continues from here, and returns the task to Claude when Claude's limit resets. To return it, write a final "Codex → Claude resume point" section and stop starting new batches. No push/release/install without the user; the GitHub repo is private.

Reference: related/asp-reference (ASP 4.3.0 by Chocohead, MC 1.12.2, decompiled sources in src/, assets in jar/). Depends on IC2 PORT 0.1.18 (../industrialcraft2-modern/versions/0.1.18/…jar, set by `ic2_version` in gradle.properties). IC2 PORT is complete through 0.1.17 (see its CODEX_HANDOFF.md).

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

## Codex batch 0.1.0 — functional ASP port complete (2026-10-16 19:42 Moscow)

Continued Claude's c827a87 WIP and its untracked items/tabs/charge-slot files. Implemented all six machines, shared IC2 EU registration/lifecycle, four charge slots, day/night/roof/weather checks, quantum generator settings/redstone, data-driven molecular recipes, all materials and three solar helmets, double slab, crafting/config switches, charge and dye preservation, vanilla dye support, GUI/network sync, helmet HUD, original model and both active plasma/particle layers, original en/ru/textures, JEI molecular page. Quantum generator stays creative-only as in the reference. Optional absent addon-output tags cannot consume input; recipes become usable when the tags are present.

Molecular processing reserves the exact input, persists pending output and EU across saves, caps accepted EU across multiple injections, pauses for a full output slot, and refunds reserved input on breaking once. Helmets charge armour before inventory/self, preserve fractional EU, and hybrid/ultimate replenish air. Panel GUI synchronizes server balance values. Original recipe switches are custom NeoForge conditions evaluated on datapack reload.

Validation: dedicated server **53 checks, 0 failed**, including actual solar→BatBox, quantum→MFSU, quantum→molecular completion, weather/time, persistence/full output/overflow, helmet charge/air/priority/fractional EU/dye/upgrade, original hard/easy/simple recipes through two actual reloads. Hidden silent client with JEI passed; inspected all six GUI screens, original models/items, dyed helmet, HUD and ASP molecular + IC2 macerator pages. No missing-model/texture or error/exception messages in final client log. Build passed. JAR/SHA256/logs/screenshots: versions/0.1.0 (gitignored). Local commits only; no push/release/install/production-server changes.

During startup found a real IC2+JEI class-init deadlock. Fixed in IC2 **0.1.18 / 0d5bc6a**, lazy machine descriptor initialization. ASP gradle.properties now requires its archived JAR; do not revert to 0.1.17.

### Resume point

Next: EMT 1.2.6.2 (../electro-magic-tools-modern/related/emt-reference/src, 120 remapped classes; skeleton has no build.gradle yet), then Gravi Suit, then combined all-mod compatibility audit. ASP+IC2 is checked; full EMT/Gravi compatibility and separate dedicated-server ASP GUI multiplayer test remain unverified. Current takeover was explicitly requested while Claude was idle (not a usage-limit reset); do not wake Claude mid-batch without a handoff. When returning to Claude, stop edits and verify real resumed tool work.


## Codex 2026-10-17 — ASP 0.1.2

Fixed the alternate double slab item replacing vanilla smooth_stone_slab's block-to-item association. Its explicit name now uses the existing EN/RU translation. Four actual placement/mapping/crafting regression checks: server58/58 passed. Build/archive versions/0.1.2 done; combined full-pack server29/29 and hidden client/JEI14/14 passed. User approved release/install in latest Claude session; latest state ../CLAUDE_CONTINUE_PORTS.md.


## Codex isolated review — 0.1.3, 2026-10-17

Branch codex/solar-charge-save; main/production untouched. Panel marksdirty when chargeSlots consumesEU even if storage0→0; signeddraw returns EnergyNet refunds and retains positiveoverdrawfloor. Requires isolatedIC2.24 energy persistencedirtyfix.
Native -Dasp.audit=persistence uses realblockticks/EnergyNet: selfcharge64EU, separatedsource/receiverchunks, MFSU/ElectricFurnace injection, signedsource draw, completeyellowwool500000EU→glowstone. Baseline16checks7fail, final16/16 with1011native ticks; generated501248-buffer1248-paid500000=0 (baseline lost224EU). No injectedenergy/manualmachine ticks in these cases.
Regular58/58 audit and build PASS; logs /tmp/asp-review-{baseline,fixed,regression,build}.log, archives versions/0.1.3. No push/release/install.


## Visual fixes — 0.1.4, 2026-10-17

The user reported four invisible helmet stacks (empty/full hybrid and ultimate) in the creative tab. The sprites and models were present; the item color handler returned 24-bit RGB, which Minecraft 1.21.1 interprets as zero-alpha ARGB. The base now returns `FastColor.ARGB32.opaque(rgb)` and the overlay returns opaque white (`-1`). Original dyeing is preserved.

`HelmetIconAudit` uses real `GuiGraphics.renderItem` without EU/durability bars, then inspects captured pixels for all eight empty/full/default/dyed variants. Baseline: 32/32 failed, every icon had zero visible pixels. Fixed: 32/32 passed, base dye and overlay invariance included; screenshots visually inspected. Existing GUI and JEI pages passed. Build passed; JAR, SHA256, baseline/fixed logs and screenshots archived in `versions/0.1.4/`. Minimum IC2 version remains 0.1.24.

This batch is local and awaits application after the player's current Prism session closes. Installed baseline remains ASP 0.1.3 / IC2 0.1.26, already published by the earlier approved Claude deployment. Do not overwrite jars used by a running Prism JVM. The new visual fixes have not been human-tested and have not been pushed or released; user review is required before publishing. IC2 companion visual fixes are in 0.1.27.

Новый набор IC2 0.1.27 / ASP 0.1.4 вместе с полным набором модов прошёл выделенный сервер совместимости 29/29 и скрытый клиент с JEI 14/14, без ERROR/FAIL. Журналы приложены к архиву этой версии.


Installation update — 2026-10-17 19:22 Moscow: user said «заменяй» and closed Prism. IC2 0.1.27 and ASP 0.1.4 now installed on production+Prism; all five principal port hashes verified at both locations. Countdown5→1, save-allflush, graceful stop, offline world backup208991731bytes and oldJARbackups (`mod-backups/codex-visual-20261017-192011/`), successful start Done1.634s,0ERROR,20TPS/average1.9ms. Exact manifest `../compat-test/CODEX_VISUAL_DEPLOY_STATE.json` and `CURRENT_DEPLOY_STATE.json`. Previous 'not installed' notes are historical. New GitHub push/releases still await review/approval; no publication was authorized by the replacement request.
