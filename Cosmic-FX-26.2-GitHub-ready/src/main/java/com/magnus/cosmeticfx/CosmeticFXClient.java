package com.magnus.cosmeticfx;

import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPlayerBlockBreakEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

import java.nio.file.Path;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Random;

public final class CosmeticFXClient {
    private static CosmeticConfig config;
    private static final Map<LivingEntity, Integer> HURT = new IdentityHashMap<>();
    private static final Map<LivingEntity, Integer> DEATH = new IdentityHashMap<>();
    private static final Random RANDOM = new Random();
    private static long flashUntil;
    private static int flashColor = 0xFFFF3B30;

    public static void init() {
        Path path = Minecraft.getInstance().gameDirectory.toPath().resolve("config/cosmeticfx.properties");
        config = new CosmeticConfig(path);
        config.load();

        ClientTickEvents.END_CLIENT_TICK.register(CosmeticFXClient::tick);
        ClientPlayerBlockBreakEvents.AFTER.register(CosmeticFXClient::blockBreak);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommands.literal("cosmic")
                        .then(ClientCommands.literal("menu").executes(ctx -> {
                            Minecraft client = ctx.getSource().getClient();
                            client.execute(() -> client.setScreen(new CosmicScreen(client.screen)));
                            return 1;
                        }))
                        .then(ClientCommands.literal("toggle").executes(ctx -> {
                            config.enabled = !config.enabled;
                            config.save();
                            feedback(ctx.getSource().getClient(), "Cosmic: " + (config.enabled ? "ON" : "OFF"));
                            return 1;
                        }))
                        .then(ClientCommands.literal("test").executes(ctx -> {
                            testEffects();
                            return 1;
                        }))
        ));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(
                ClientCommands.literal("cosmeticfx")
                        .then(ClientCommands.literal("toggle").executes(ctx -> {
                            config.enabled = !config.enabled;
                            config.save();
                            feedback(ctx.getSource().getClient(), "CosmeticFX: " + (config.enabled ? "ON" : "OFF"));
                            return 1;
                        }))
                        .then(ClientCommands.literal("reload").executes(ctx -> {
                            config.load();
                            feedback(ctx.getSource().getClient(), "CosmeticFX config reloaded.");
                            return 1;
                        }))
                        .then(ClientCommands.literal("test").executes(ctx -> {
                            Minecraft client = ctx.getSource().getClient();
                            if (client.player != null) burst(client, client.player, config.hitCount, ParticleTypes.CRIT);
                            return 1;
                        }))
        ));

        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("cosmeticfx", "flash"),
                CosmeticFXClient::renderFlash);
    }

    public static CosmeticConfig config() { return config; }

    public static void saveConfig() { config.save(); }

    public static void testEffects() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            burst(client, client.player, config.hitCount, choose(config.hitParticle));
            if (config.critBurst) burst(client, client.player, config.critCount, choose(config.critParticle));
            if (config.hitFlash) flash(0xFF8B5CFF);
        }
    }

    private static void tick(Minecraft client) {
        if (!config.enabled || client.level == null) return;
        for (Entity raw : client.level.entitiesForRendering()) {
            if (!(raw instanceof LivingEntity entity)) continue;
            int hurt = entity.hurtTime;
            int oldHurt = HURT.getOrDefault(entity, 0);
            if (hurt > 0 && oldHurt == 0) {
                if (config.hitParticles) burst(client, entity, config.hitCount, choose(config.hitParticle));
                if (config.critBurst) burst(client, entity, config.critCount, choose(config.critParticle));
                if (config.hitFlash && entity == client.player) flash(0xFFFF3B30);
            }
            HURT.put(entity, hurt);

            int death = entity.deathTime;
            int oldDeath = DEATH.getOrDefault(entity, 0);
            if (config.killBurst && death > 0 && oldDeath == 0) {
                burst(client, entity, config.killCount, choose(config.killParticle));
            }
            DEATH.put(entity, death);
        }
        if (HURT.size() > 4096) HURT.clear();
        if (DEATH.size() > 4096) DEATH.clear();
    }

    private static void blockBreak(net.minecraft.client.multiplayer.ClientLevel level, net.minecraft.client.player.LocalPlayer player, BlockPos pos, BlockState state) {
        if (!config.enabled || !config.blockBreakEffects) return;
        for (int i = 0; i < config.blockCount; i++) {
            double x = pos.getX() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.8;
            double y = pos.getY() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.8;
            double z = pos.getZ() + 0.5 + (RANDOM.nextDouble() - 0.5) * 0.8;
            level.addParticle(ParticleTypes.POOF, x, y, z,
                    (RANDOM.nextDouble() - 0.5) * config.speed,
                    RANDOM.nextDouble() * config.speed + config.verticalBias,
                    (RANDOM.nextDouble() - 0.5) * config.speed);
        }
    }

    private static void burst(Minecraft client, LivingEntity entity, int count, net.minecraft.core.particles.ParticleOptions particle) {
        if (client.level == null) return;
        double cx = entity.getX();
        double cy = entity.getY() + entity.getBbHeight() * 0.55;
        double cz = entity.getZ();
        for (int i = 0; i < Math.max(0, count); i++) {
            double dx = (RANDOM.nextDouble() - 0.5) * config.spread;
            double dy = (RANDOM.nextDouble() - 0.5) * config.spread + config.verticalBias;
            double dz = (RANDOM.nextDouble() - 0.5) * config.spread;
            client.level.addParticle(particle,
                    cx + dx, cy + dy, cz + dz,
                    dx * config.speed * 3.0,
                    dy * config.speed * 3.0,
                    dz * config.speed * 3.0);
        }
    }

    private static net.minecraft.core.particles.ParticleOptions choose(String name) {
        return switch (name.toLowerCase()) {
            case "crit" -> ParticleTypes.CRIT;
            case "enchanted_hit" -> ParticleTypes.ENCHANTED_HIT;
            case "damage_indicator" -> ParticleTypes.DAMAGE_INDICATOR;
            case "end_rod" -> ParticleTypes.END_ROD;
            case "explosion" -> ParticleTypes.EXPLOSION;
            case "poof" -> ParticleTypes.POOF;
            case "flame" -> ParticleTypes.FLAME;
            case "cloud" -> ParticleTypes.CLOUD;
            default -> ParticleTypes.CRIT;
        };
    }

    private static void flash(int color) {
        flashColor = color;
        flashUntil = System.currentTimeMillis() + Math.max(0, config.flashDurationMs);
    }

    private static void renderFlash(GuiGraphicsExtractor graphics, DeltaTracker delta) {
        if (!config.enabled || !config.hitFlash) return;
        long remaining = flashUntil - System.currentTimeMillis();
        if (remaining <= 0) return;
        float t = Math.min(1f, remaining / (float)Math.max(1, config.flashDurationMs));
        int alpha = (int)(config.flashAlpha * t);
        graphics.fill(0, 0, graphics.guiWidth(), graphics.guiHeight(), (alpha << 24) | (flashColor & 0xFFFFFF));
    }

    private static void feedback(Minecraft client, String text) {
        if (client.player != null) client.player.sendSystemMessage(Component.literal(text));
    }
}
