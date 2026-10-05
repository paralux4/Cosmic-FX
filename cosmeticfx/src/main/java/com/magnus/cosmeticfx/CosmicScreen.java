package com.magnus.cosmeticfx;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * In-game configuration screen for Cosmic.  Everything here controls visual-only client effects.
 */
public final class CosmicScreen extends Screen {
    private final Screen parent;

    public CosmicScreen(Screen parent) {
        super(Component.literal("COSMIC"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int center = this.width / 2;
        int left = center - 155;
        int right = center + 5;
        int y = 62;
        int gap = 30;

        addRenderableWidget(Button.builder(label("Master", CosmeticFXClient.config().enabled), b -> {
            CosmeticFXClient.config().enabled = !CosmeticFXClient.config().enabled;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Master", CosmeticFXClient.config().enabled));
        }).bounds(left, y, 150, 22).build());

        addRenderableWidget(Button.builder(label("Hit Particles", CosmeticFXClient.config().hitParticles), b -> {
            CosmeticFXClient.config().hitParticles = !CosmeticFXClient.config().hitParticles;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Hit Particles", CosmeticFXClient.config().hitParticles));
        }).bounds(right, y, 150, 22).build());

        y += gap;
        addRenderableWidget(Button.builder(label("Critical Burst", CosmeticFXClient.config().critBurst), b -> {
            CosmeticFXClient.config().critBurst = !CosmeticFXClient.config().critBurst;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Critical Burst", CosmeticFXClient.config().critBurst));
        }).bounds(left, y, 150, 22).build());

        addRenderableWidget(Button.builder(label("Kill Burst", CosmeticFXClient.config().killBurst), b -> {
            CosmeticFXClient.config().killBurst = !CosmeticFXClient.config().killBurst;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Kill Burst", CosmeticFXClient.config().killBurst));
        }).bounds(right, y, 150, 22).build());

        y += gap;
        addRenderableWidget(Button.builder(label("Hit Flash", CosmeticFXClient.config().hitFlash), b -> {
            CosmeticFXClient.config().hitFlash = !CosmeticFXClient.config().hitFlash;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Hit Flash", CosmeticFXClient.config().hitFlash));
        }).bounds(left, y, 150, 22).build());

        addRenderableWidget(Button.builder(label("Block FX", CosmeticFXClient.config().blockBreakEffects), b -> {
            CosmeticFXClient.config().blockBreakEffects = !CosmeticFXClient.config().blockBreakEffects;
            CosmeticFXClient.saveConfig();
            b.setMessage(label("Block FX", CosmeticFXClient.config().blockBreakEffects));
        }).bounds(right, y, 150, 22).build());

        y += gap;
        addRenderableWidget(Button.builder(Component.literal("Hit Count: " + CosmeticFXClient.config().hitCount), b -> {
            CosmeticFXClient.config().hitCount = next(CosmeticFXClient.config().hitCount, 1, 32);
            CosmeticFXClient.saveConfig();
            b.setMessage(Component.literal("Hit Count: " + CosmeticFXClient.config().hitCount));
        }).bounds(left, y, 150, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Crit Count: " + CosmeticFXClient.config().critCount), b -> {
            CosmeticFXClient.config().critCount = next(CosmeticFXClient.config().critCount, 1, 32);
            CosmeticFXClient.saveConfig();
            b.setMessage(Component.literal("Crit Count: " + CosmeticFXClient.config().critCount));
        }).bounds(right, y, 150, 22).build());

        y += gap;
        addRenderableWidget(Button.builder(Component.literal("Spread: " + fmt(CosmeticFXClient.config().spread)), b -> {
            CosmeticFXClient.config().spread = next(CosmeticFXClient.config().spread, 0.05, 0.60, 0.05);
            CosmeticFXClient.saveConfig();
            b.setMessage(Component.literal("Spread: " + fmt(CosmeticFXClient.config().spread)));
        }).bounds(left, y, 150, 22).build());

        addRenderableWidget(Button.builder(Component.literal("Speed: " + fmt(CosmeticFXClient.config().speed)), b -> {
            CosmeticFXClient.config().speed = next(CosmeticFXClient.config().speed, 0.05, 0.50, 0.05);
            CosmeticFXClient.saveConfig();
            b.setMessage(Component.literal("Speed: " + fmt(CosmeticFXClient.config().speed)));
        }).bounds(right, y, 150, 22).build());

        y += 38;
        addRenderableWidget(Button.builder(Component.literal("Test Effects"), b -> CosmeticFXClient.testEffects())
                .bounds(left, y, 150, 22).build());
        addRenderableWidget(Button.builder(Component.literal("Close"), b -> onClose())
                .bounds(right, y, 150, 22).build());
    }

    private static Component label(String name, boolean enabled) {
        return Component.literal(name + ": " + (enabled ? "ON" : "OFF"));
    }

    private static int next(int value, int min, int max) {
        return value >= max ? min : value + 1;
    }

    private static double next(double value, double min, double max, double step) {
        double result = value + step;
        return result > max + 0.0001 ? min : Math.round(result * 100.0) / 100.0;
    }

    private static String fmt(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        int center = this.width / 2;
        graphics.drawCenteredString(this.font, Component.literal("COSMIC"), center, 18, 0xFFFFFF);
        graphics.drawCenteredString(this.font, Component.literal("Visual FX • client-side only"), center, 36, 0xA0A0B0);
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}
