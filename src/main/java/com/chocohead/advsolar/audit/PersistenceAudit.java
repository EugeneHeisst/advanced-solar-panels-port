package com.chocohead.advsolar.audit;

import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.ref.AspBlocks;
import com.chocohead.advsolar.ref.AspRecipes;
import com.chocohead.advsolar.tiles.TileEntityMolecularAssembler;
import com.chocohead.advsolar.tiles.TileEntitySolarPanel;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.item.ElectricItem;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.ref.Ic2Blocks;
import ic2.core.ref.Ic2Items;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * -Dasp.audit=persistence. Ordinary block placement, native block-entity ticks and the actual EnergyNet.
 * Clearing a chunk's unsaved flag models the save gate immediately after a successful chunk save;
 * each tested receiving chunk is separated from its source so another machine cannot hide missing dirty calls.
 * The molecular phase processes the full original 500000-EU recipe, including its final partial packet.
 */
@EventBusSubscriber(modid = AdvancedSolarPanels.MODID)
public final class PersistenceAudit {
    private static final double EPSILON = 0.001;
    private static final Set<Long> FORCED_BY_AUDIT = new HashSet<>();
    private static ServerLevel level;
    private static BlockPos origin, balancedPos, storageSourcePos, storagePos, machineSourcePos, machinePos, refundSourcePos, molecularPos;
    private static TileEntitySolarPanel balanced, storageSource, machineSource, refundSource;
    private static TileEntityElectricBlock storage;
    private static TileEntityElectricMachine machine;
    private static TileEntityMolecularAssembler molecular;
    private static int tick, stage, armedTicker, dayTicker, refundTickerBefore, roofTick = -1, checks, failed, oldRandomTicks;
    private static double chargeBefore, storageBefore, machineBefore, generated, productionForNativeTick;
    private static boolean done, noDuplicateEnergy = true;

    private PersistenceAudit() {}

    private static boolean enabled() { return "persistence".equals(System.getProperty("asp.audit")); }

    private static void check(String name, boolean passed, Object detail) {
        checks++;
        if (!passed) failed++;
        AdvancedSolarPanels.LOG.info("[asp-persistence-audit] {} {} {}", passed ? "PASS" : "FAIL", name, detail);
    }

    private static void clearSky(BlockPos pos) {
        for (int y = pos.getY() + 1; y < level.getMaxBuildHeight(); y++) level.setBlock(new BlockPos(pos.getX(), y, pos.getZ()), Blocks.AIR.defaultBlockState(), 3);
    }

    private static void forceAround(BlockPos pos) {
        for (int x = -1; x <= 1; x++) for (int z = -1; z <= 1; z++) {
            int cx = (pos.getX() >> 4) + x, cz = (pos.getZ() >> 4) + z;
            long packed = ChunkPos.asLong(cx, cz);
            if (!level.getForcedChunks().contains(packed)) {
                level.setChunkForced(cx, cz, true);
                FORCED_BY_AUDIT.add(packed);
            }
        }
    }

    private static TileEntitySolarPanel solar(BlockPos pos, boolean advanced) {
        clearSky(pos);
        level.setBlock(pos, (advanced ? AspBlocks.ADVANCED_SOLAR_PANEL : AspBlocks.ULTIMATE_SOLAR_PANEL).get().defaultBlockState(), 3);
        return (TileEntitySolarPanel)level.getBlockEntity(pos);
    }

    @SubscribeEvent
    static void start(ServerStartedEvent event) {
        if (!enabled()) return;
        level = event.getServer().overworld();
        oldRandomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        level.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(0, event.getServer());
        try {
            origin = new BlockPos((1024 + level.random.nextInt(64)) * 16, Math.min(220, level.getMaxBuildHeight() - 16), 16384);
            balancedPos = origin.offset(4, 0, 4);
            storageSourcePos = origin.offset(31, 0, 4);
            storagePos = origin.offset(33, 0, 4);
            machineSourcePos = origin.offset(63, 0, 4);
            machinePos = origin.offset(65, 0, 4);
            refundSourcePos = origin.offset(100, 0, 4);
            molecularPos = refundSourcePos.east(2);
            for (BlockPos pos : new BlockPos[]{balancedPos, storageSourcePos, storagePos, machineSourcePos, machinePos, refundSourcePos, molecularPos}) forceAround(pos);
            for (BlockPos pos : BlockPos.betweenClosed(origin.offset(0, -1, 2), origin.offset(104, 2, 6))) level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            level.setDayTime(18000);
            level.setWeatherParameters(10000, 0, false, false);
            balanced = solar(balancedPos, false);
            balanced.chargeSlots.put(0, new ItemStack(Ic2Items.ENERGY_PACK.get()));
            storageSource = solar(storageSourcePos, false);
            level.setBlock(storageSourcePos.east(), Ic2Blocks.CABLES.get(CableType.glass_fibre).get().defaultBlockState(), 3);
            level.setBlock(storagePos, Ic2Blocks.MFSU.get().defaultBlockState().setValue(BlockTileEntity.FACING, Direction.EAST), 3);
            storage = (TileEntityElectricBlock)level.getBlockEntity(storagePos);
            machineSource = solar(machineSourcePos, true);
            level.setBlock(machineSourcePos.east(), Ic2Blocks.CABLES.get(CableType.glass_fibre).get().defaultBlockState(), 3);
            level.setBlock(machinePos, Ic2Blocks.ELECTRIC_FURNACE.get().defaultBlockState(), 3);
            machine = (TileEntityElectricMachine)level.getBlockEntity(machinePos);
            check("each receiving chunk is separate from every dirty source", new ChunkPos(balancedPos).toLong() != new ChunkPos(storageSourcePos).toLong()
                && new ChunkPos(storagePos).toLong() != new ChunkPos(storageSourcePos).toLong()
                && new ChunkPos(machinePos).toLong() != new ChunkPos(machineSourcePos).toLong(), "isolated chunk boundaries");
        } catch (Throwable error) { crash(error); }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void beforeLevel(LevelTickEvent.Pre event) {
        if (!enabled() || done || event.getLevel() != level || refundSource == null) return;
        refundTickerBefore = refundSource.ticker;
        productionForNativeTick = refundSource.isLoaded() ? Math.max(0, Math.min(refundSource.maxStorage - refundSource.storage, refundSource.output())) : 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void afterLevel(LevelTickEvent.Post event) {
        if (!enabled() || done || event.getLevel() != level || refundSource == null) return;
        int executed = refundSource.ticker - refundTickerBefore;
        if (executed == 1) generated += productionForNativeTick;
        else if (executed != 0) crash(new IllegalStateException("Unexpected native source update count " + executed));
    }

    @SubscribeEvent
    static void tick(ServerTickEvent.Post event) {
        if (!enabled() || done || level == null || balanced == null) return;
        tick++;
        try {
            if (stage == 0 && balanced.ticker >= 20 && storageSource.ticker >= 20 && machineSource.ticker >= 20 && storage.energy > 0 && machine.energy > 0) {
                check("ultimate solar natively charges an IC2 energy pack at night", balanced.output() == 64 && balanced.storage == 0 && ElectricItem.manager.getCharge(balanced.chargeSlots.get(0)) > 0, ElectricItem.manager.getCharge(balanced.chargeSlots.get(0)));
                chargeBefore = ElectricItem.manager.getCharge(balanced.chargeSlots.get(0));
                armedTicker = balanced.ticker;
                level.getChunkAt(balancedPos).setUnsaved(false);
                stage = 1;
            } else if (stage == 1 && balanced.ticker > armedTicker) {
                double charge = ElectricItem.manager.getCharge(balanced.chargeSlots.get(0));
                check("balanced native tick transfers exactly 64 EU while the solar buffer remains zero", charge == chargeBefore + 64 && balanced.storage == 0, charge + " / buffer " + balanced.storage);
                check("changing only the charged item marks the solar chunk dirty", level.getChunkAt(balancedPos).isUnsaved(), "native ItemStack charge changed");
                // Test the same signed IEnergySource call that EnergyNet uses when a sink returns unused EU.
                balanced.storage = 100;
                ((IEnergySource)balanced).drawEnergy(-32);
                check("negative EnergyNet draw refunds exactly 32 EU to the solar source", balanced.storage == 132, balanced.storage);
                balanced.drawEnergy(1000);
                check("positive overdraw still clamps the source at zero", balanced.storage == 0, balanced.storage);
                level.setDayTime(6000);
                storageSource.checkTheSky();
                machineSource.checkTheSky();
                dayTicker = storageSource.ticker;
                stage = 2;
            } else if (stage == 2 && storageSource.ticker >= dayTicker + 3) {
                check("empty MFSU and electric furnace receive native EU without inventory work", storage.energy > 0 && machine.energy > 0 && storage.chargeSlot.isEmpty() && storage.dischargeSlot.isEmpty() && machine.dischargeSlot.isEmpty(), storage.energy + " / " + machine.energy);
                storageBefore = storage.energy;
                machineBefore = machine.energy;
                level.getChunkAt(storagePos).setUnsaved(false);
                level.getChunkAt(machinePos).setUnsaved(false);
                stage = 3;
            } else if (stage == 3 && storage.energy > storageBefore && machine.energy > machineBefore) {
                check("native network delivery increases both receiver buffers", storage.energy > storageBefore && machine.energy > machineBefore, storage.energy + " / " + machine.energy);
                check("MFSU network injection marks its separate chunk dirty", level.getChunkAt(storagePos).isUnsaved(), "no charging/discharging inventory or source in chunk");
                check("idle electric furnace network injection marks its separate chunk dirty", level.getChunkAt(machinePos).isUnsaved(), "no recipe or inventory work");
                storage.energy = 100;
                level.getChunkAt(storagePos).setUnsaved(false);
                ((IEnergySource)storage).drawEnergy(32);
                check("MFSU source draw consumes exactly 32 EU and marks storage dirty", storage.energy == 68 && level.getChunkAt(storagePos).isUnsaved(), storage.energy);
                level.getChunkAt(storagePos).setUnsaved(false);
                ((IEnergySource)storage).drawEnergy(-32);
                check("MFSU source refund restores exactly 32 EU and marks storage dirty", storage.energy == 100 && level.getChunkAt(storagePos).isUnsaved(), storage.energy);
                startMolecular();
                stage = 4;
            } else if (stage == 4) {
                double paid = molecular.outputSlot.get().is(Items.GLOWSTONE) ? 500000 : molecular.energyUsed;
                double accounted = refundSource.storage + paid;
                noDuplicateEnergy &= Double.isFinite(accounted) && accounted <= generated + EPSILON;
                if (roofTick < 0 && molecular.outputSlot.get().is(Items.GLOWSTONE)) {
                    check("full original molecular recipe completes on native EU ticks", molecular.outputSlot.get().getCount() == 1 && molecular.inputSlot.isEmpty() && molecular.reserved.isEmpty(), molecular.outputSlot.get());
                    level.setBlock(refundSourcePos.above(), Blocks.STONE.defaultBlockState(), 3);
                    refundSource.checkTheSky();
                    roofTick = tick;
                } else if (roofTick >= 0 && tick - roofTick == 4) {
                    check("native final partial packet is refunded without losing EU", Math.abs(generated - (refundSource.storage + 500000)) < EPSILON,
                        "generated=" + generated + " buffer=" + refundSource.storage + " paid=500000 delta=" + (generated - refundSource.storage - 500000));
                    check("native molecular network never duplicates EU", noDuplicateEnergy, "observed every server tick");
                    finish();
                }
            }
            if (tick > 1400) crash(new IllegalStateException("Timed out in persistence audit stage " + stage));
        } catch (Throwable error) { crash(error); }
    }

    private static void startMolecular() {
        refundSource = solar(refundSourcePos, false);
        level.setBlock(refundSourcePos.east(), Ic2Blocks.CABLES.get(CableType.glass_fibre).get().defaultBlockState(), 3);
        level.setBlock(molecularPos, AspBlocks.MOLECULAR_TRANSFORMER.get().defaultBlockState(), 3);
        molecular = (TileEntityMolecularAssembler)level.getBlockEntity(molecularPos);
        var recipe = level.getRecipeManager().getRecipeFor(AspRecipes.MOLECULAR.get(), new SingleRecipeInput(new ItemStack(Items.YELLOW_WOOL)), level).orElseThrow().value();
        check("real yellow-wool molecular recipe costs 500000 EU and produces glowstone", recipe.energy() == 500000 && recipe.result(level.registryAccess()).is(Items.GLOWSTONE), recipe.energy());
        molecular.inputSlot.put(new ItemStack(Items.YELLOW_WOOL));
        // No reserved recipe, prepaid EU, direct injectEnergy, charge call or manual machine tick.
    }

    private static void crash(Throwable error) {
        failed++;
        AdvancedSolarPanels.LOG.error("[asp-persistence-audit] FAIL audit exception", error);
        finish();
    }

    private static void finish() {
        if (done) return;
        done = true;
        try {
            if (balanced != null) balanced.chargeSlots.clear();
            if (molecular != null) { molecular.reserved = ItemStack.EMPTY; molecular.inputSlot.clear(); molecular.outputSlot.clear(); }
            if (origin != null) for (BlockPos pos : BlockPos.betweenClosed(origin.offset(0, 0, 2), origin.offset(104, 1, 6))) level.removeBlock(pos, false);
        } catch (Throwable error) {
            failed++;
            AdvancedSolarPanels.LOG.error("[asp-persistence-audit] FAIL cleanup", error);
        } finally {
            level.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).set(oldRandomTicks, level.getServer());
            for (long packed : FORCED_BY_AUDIT) level.setChunkForced(ChunkPos.getX(packed), ChunkPos.getZ(packed), false);
            AdvancedSolarPanels.LOG.info("[asp-persistence-audit] DONE {} checks, {} failed; {} native server ticks", checks, failed, tick);
            level.getServer().halt(false);
        }
    }
}
