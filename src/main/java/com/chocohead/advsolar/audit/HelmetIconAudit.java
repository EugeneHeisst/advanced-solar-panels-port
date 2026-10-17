package com.chocohead.advsolar.audit;

import com.chocohead.advsolar.AdvancedSolarPanels;
import com.chocohead.advsolar.ref.AspItems;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Actual GUI pixels: a valid model and a JEI entry can still be completely transparent. */
final class HelmetIconAudit extends Screen {
    private static final int ICON_SCALE = 3;
    private static final int BACKGROUND = 0xff17372b;
    private static HelmetIconAudit active;
    private final List<ItemStack> stacks = new ArrayList<>();
    private int frames, checks, failed;
    private boolean complete;

    private HelmetIconAudit() {
        super(Component.literal("ASP helmet icon audit"));
        for (var helmet : List.of(AspItems.HYBRID_SOLAR_HELMET.get(), AspItems.ULTIMATE_SOLAR_HELMET.get())) {
            for (boolean dyed : new boolean[]{false, true}) {
                for (boolean charged : new boolean[]{false, true}) {
                    ItemStack stack = charged ? helmet.charged() : new ItemStack(helmet);
                    if (dyed) stack.set(DataComponents.DYED_COLOR, new DyedItemColor(0xff3030, true));
                    stacks.add(stack);
                }
            }
        }
    }

    static void open(Minecraft mc) {
        active = new HelmetIconAudit();
        mc.setScreen(active);
    }

    static void finish() {
        if (active == null) {
            AdvancedSolarPanels.LOG.error("[asp-helmet-icons] FAIL audit screen never opened");
            AdvancedSolarPanels.LOG.info("[asp-helmet-icons] DONE 1 checks, 1 failed");
            return;
        }
        if (!active.complete) active.check("GUI rendered and captured", false, active.frames);
        AdvancedSolarPanels.LOG.info("[asp-helmet-icons] DONE {} checks, {} failed", active.checks, active.failed);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, width, height, 0xff0d1320);
        graphics.drawString(font, "ASP native item icons: empty / full / dyed empty / dyed full", 8, 8, -1);
        for (int i = 0; i < stacks.size(); i++) {
            int x = x(i), y = y(i);
            graphics.drawString(font, i < 4 ? "Hybrid" : "Ultimate", x, y - 12, -1);
            graphics.fill(x, y, x + 16 * ICON_SCALE, y + 16 * ICON_SCALE, BACKGROUND);
            graphics.pose().pushPose();
            graphics.pose().translate(x, y, 0);
            graphics.pose().scale(ICON_SCALE, ICON_SCALE, 1);
            // Deliberately omit decorations: an EU bar must not make an invisible icon pass.
            graphics.renderItem(stacks.get(i), 0, 0);
            graphics.pose().popPose();
        }
        graphics.flush();
        if (++frames >= 3 && !complete) {
            complete = true;
            try (NativeImage image = Screenshot.takeScreenshot(minecraft.getMainRenderTarget())) {
                Files.createDirectories(Path.of("audit"));
                image.writeToFile(Path.of("audit", "helmet_icons.png"));
                inspect(image);
            } catch (Throwable error) {
                check("GUI pixel inspection completed", false, error.toString());
                AdvancedSolarPanels.LOG.error("[asp-helmet-icons] pixel inspection failed", error);
            }
        }
    }

    private void inspect(NativeImage image) {
        double scale = minecraft.getWindow().getGuiScale();
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            String name = name(i);
            for (int layer = 0; layer < 2; layer++) {
                int color = minecraft.getItemColors().getColor(stack, layer);
                check(name + " layer " + layer + " native color is opaque", FastColor.ARGB32.alpha(color) == 255,
                    String.format("%08x", color));
            }
            int visible = 0;
            int left = (int)Math.ceil(x(i) * scale), top = (int)Math.ceil(y(i) * scale);
            int right = (int)Math.floor((x(i) + 16 * ICON_SCALE) * scale);
            int bottom = (int)Math.floor((y(i) + 16 * ICON_SCALE) * scale);
            for (int py = top; py < bottom; py++) for (int px = left; px < right; px++) {
                int pixel = image.getPixelRGBA(px, py);
                if (FastColor.ABGR32.red(pixel) != 0x17 || FastColor.ABGR32.green(pixel) != 0x37 || FastColor.ABGR32.blue(pixel) != 0x2b) visible++;
            }
            int minimum = (int)(20 * ICON_SCALE * ICON_SCALE * scale * scale);
            check(name + " native GUI has visible icon pixels", visible > minimum, visible + " > " + minimum);
        }
        for (int helmet = 0; helmet < 2; helmet++) for (int charge = 0; charge < 2; charge++) {
            int white = helmet * 4 + charge, dyed = white + 2;
            // Both original sprites have a base-only pixel at (6,5), and an overlay pixel at (8,6).
            int whiteBase = sample(image, white, 6, 5), redBase = sample(image, dyed, 6, 5);
            boolean grayBase = channelDistance(whiteBase, FastColor.ABGR32.color(255, 229, 229, 229)) < 80
                && Math.abs(FastColor.ABGR32.red(whiteBase) - FastColor.ABGR32.green(whiteBase)) <= 3
                && Math.abs(FastColor.ABGR32.red(whiteBase) - FastColor.ABGR32.blue(whiteBase)) <= 3;
            boolean redTint = FastColor.ABGR32.red(redBase) > 100
                && FastColor.ABGR32.red(redBase) > 3 * FastColor.ABGR32.green(redBase)
                && FastColor.ABGR32.red(redBase) > 3 * FastColor.ABGR32.blue(redBase);
            check(name(dyed) + " native GUI dyes the helmet base", grayBase && redTint,
                String.format("white=%08x red=%08x", whiteBase, redBase));
            int whiteOverlay = sample(image, white, 8, 6), redOverlay = sample(image, dyed, 8, 6);
            boolean visibleOverlay = FastColor.ABGR32.blue(whiteOverlay) > 100
                && FastColor.ABGR32.green(whiteOverlay) > 50
                && FastColor.ABGR32.blue(whiteOverlay) > FastColor.ABGR32.red(whiteOverlay);
            check(name(dyed) + " native GUI preserves the colored overlay", visibleOverlay && channelDistance(whiteOverlay, redOverlay) <= 2,
                String.format("white=%08x red=%08x", whiteOverlay, redOverlay));
        }
    }

    private int sample(NativeImage image, int index, int u, int v) {
        double scale = minecraft.getWindow().getGuiScale();
        return image.getPixelRGBA((int)((x(index) + (u + .5) * ICON_SCALE) * scale),
            (int)((y(index) + (v + .5) * ICON_SCALE) * scale));
    }

    private static int channelDistance(int a, int b) {
        return Math.max(Math.abs(FastColor.ABGR32.red(a) - FastColor.ABGR32.red(b)),
            Math.max(Math.abs(FastColor.ABGR32.green(a) - FastColor.ABGR32.green(b)),
                Math.abs(FastColor.ABGR32.blue(a) - FastColor.ABGR32.blue(b))));
    }

    private static int x(int i) { return 16 + (i % 4) * 76; }
    private static int y(int i) { return 40 + (i / 4) * 82; }
    private static String name(int i) {
        return (i < 4 ? "hybrid" : "ultimate") + (i % 2 == 0 ? " empty" : " full") + (i % 4 < 2 ? " white" : " dyed");
    }

    private void check(String name, boolean ok, Object value) {
        checks++;
        if (!ok) failed++;
        AdvancedSolarPanels.LOG.info("[asp-helmet-icons] {} {} {}", ok ? "PASS" : "FAIL", name, value);
    }
}
