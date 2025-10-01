package com.github.galysso.structures_features.platform.neoforge;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructuresStorage;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(StructuresFeatures.MOD_ID)
public final class StructuresFeaturesMain {
    public StructuresFeaturesMain(IEventBus modEventBus) {
        StructuresFeatures.LOGGER.info("Initialization (NeoForge).");

        // --- Événements SERVEUR (GAME BUS) ---
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onLevelLoad);
        NeoForge.EVENT_BUS.addListener(this::onClientStarted);
    }

    private void onServerStarted(final ServerStartedEvent event) {
        StructuresFeatures.serverInit(event.getServer());
    }

    private void onLevelLoad(final LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            StructuresStorage.get(serverLevel);
        }
    }

    private void onClientStarted(final FMLClientSetupEvent event) {
        StructuresFeatures.clientInit(net.minecraft.client.Minecraft.getInstance());
    }
}
