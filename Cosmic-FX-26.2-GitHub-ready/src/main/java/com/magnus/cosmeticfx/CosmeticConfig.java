package com.magnus.cosmeticfx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CosmeticConfig {
    public boolean enabled = true;
    public boolean hitParticles = true;
    public boolean critBurst = true;
    public boolean killBurst = true;
    public boolean hitFlash = true;
    public boolean blockBreakEffects = true;
    public boolean worldPulse = true;

    public int hitCount = 8;
    public int critCount = 5;
    public int killCount = 16;
    public int blockCount = 10;
    public double spread = 0.22;
    public double speed = 0.16;
    public double verticalBias = 0.05;
    public int flashDurationMs = 90;
    public int flashAlpha = 48;

    public String hitParticle = "crit";
    public String critParticle = "enchanted_hit";
    public String killParticle = "end_rod";
    public String blockParticle = "poof";

    private final Path path;

    public CosmeticConfig(Path path) { this.path = path; }

    public void load() {
        try {
            if (!Files.exists(path)) { save(); return; }
            Properties p = new Properties();
            try (InputStream in = Files.newInputStream(path)) { p.load(in); }
            enabled = bool(p, "enabled", enabled);
            hitParticles = bool(p, "hitParticles", hitParticles);
            critBurst = bool(p, "critBurst", critBurst);
            killBurst = bool(p, "killBurst", killBurst);
            hitFlash = bool(p, "hitFlash", hitFlash);
            blockBreakEffects = bool(p, "blockBreakEffects", blockBreakEffects);
            worldPulse = bool(p, "worldPulse", worldPulse);
            hitCount = integer(p, "hitCount", hitCount);
            critCount = integer(p, "critCount", critCount);
            killCount = integer(p, "killCount", killCount);
            blockCount = integer(p, "blockCount", blockCount);
            spread = decimal(p, "spread", spread);
            speed = decimal(p, "speed", speed);
            verticalBias = decimal(p, "verticalBias", verticalBias);
            flashDurationMs = integer(p, "flashDurationMs", flashDurationMs);
            flashAlpha = integer(p, "flashAlpha", flashAlpha);
            hitParticle = p.getProperty("hitParticle", hitParticle);
            critParticle = p.getProperty("critParticle", critParticle);
            killParticle = p.getProperty("killParticle", killParticle);
            blockParticle = p.getProperty("blockParticle", blockParticle);
        } catch (IOException ignored) {}
    }

    public void save() {
        try {
            Files.createDirectories(path.getParent());
            Properties p = new Properties();
            p.setProperty("enabled", Boolean.toString(enabled));
            p.setProperty("hitParticles", Boolean.toString(hitParticles));
            p.setProperty("critBurst", Boolean.toString(critBurst));
            p.setProperty("killBurst", Boolean.toString(killBurst));
            p.setProperty("hitFlash", Boolean.toString(hitFlash));
            p.setProperty("blockBreakEffects", Boolean.toString(blockBreakEffects));
            p.setProperty("worldPulse", Boolean.toString(worldPulse));
            p.setProperty("hitCount", Integer.toString(hitCount));
            p.setProperty("critCount", Integer.toString(critCount));
            p.setProperty("killCount", Integer.toString(killCount));
            p.setProperty("blockCount", Integer.toString(blockCount));
            p.setProperty("spread", Double.toString(spread));
            p.setProperty("speed", Double.toString(speed));
            p.setProperty("verticalBias", Double.toString(verticalBias));
            p.setProperty("flashDurationMs", Integer.toString(flashDurationMs));
            p.setProperty("flashAlpha", Integer.toString(flashAlpha));
            p.setProperty("hitParticle", hitParticle);
            p.setProperty("critParticle", critParticle);
            p.setProperty("killParticle", killParticle);
            p.setProperty("blockParticle", blockParticle);
            try (OutputStream out = Files.newOutputStream(path)) { p.store(out, "CosmeticFX - client-side visual effects only"); }
        } catch (IOException ignored) {}
    }

    private static boolean bool(Properties p, String k, boolean d) { return Boolean.parseBoolean(p.getProperty(k, Boolean.toString(d))); }
    private static int integer(Properties p, String k, int d) { try { return Integer.parseInt(p.getProperty(k, Integer.toString(d))); } catch (NumberFormatException e) { return d; } }
    private static double decimal(Properties p, String k, double d) { try { return Double.parseDouble(p.getProperty(k, Double.toString(d))); } catch (NumberFormatException e) { return d; } }
}
