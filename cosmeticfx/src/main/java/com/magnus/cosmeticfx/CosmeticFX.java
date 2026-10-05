package com.magnus.cosmeticfx;

import net.fabricmc.api.ClientModInitializer;

public final class CosmeticFX implements ClientModInitializer {
    @Override public void onInitializeClient() { CosmeticFXClient.init(); }
}
