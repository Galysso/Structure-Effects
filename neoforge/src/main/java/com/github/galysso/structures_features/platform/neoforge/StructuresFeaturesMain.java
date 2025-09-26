package com.github.galysso.structures_features.platform.neoforge;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.config.ServerNamesSetsConfig;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@Mod(StructuresFeatures.MOD_ID)
public final class StructuresFeaturesMain {
    private static ServerNamesSetsConfig SERVER_NAMES_SETS_CONFIG;

    public StructuresFeaturesMain(IEventBus modEventBus) {
        StructuresFeatures.LOGGER.info("Initialization (NeoForge).");

        // --- Événements SERVEUR (GAME BUS) ---
        NeoForge.EVENT_BUS.addListener(this::onServerStarted);
        NeoForge.EVENT_BUS.addListener(this::onLevelLoad);
    }

    private void onServerStarted(final ServerStartedEvent event) {
        var server = event.getServer();
        ServerAccessor.setServerInstance(server);

        if (SERVER_NAMES_SETS_CONFIG == null) {
            SERVER_NAMES_SETS_CONFIG = new ServerNamesSetsConfig();
            StructureNaming.get(server.overworld());
            StructureNaming.init();
            for (ServerLevel level : server.getAllLevels()) {
                StructuresStorage.get(level);
            }
        }
    }

    private void onLevelLoad(final LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            StructuresStorage.get(serverLevel);
        }
    }
}
