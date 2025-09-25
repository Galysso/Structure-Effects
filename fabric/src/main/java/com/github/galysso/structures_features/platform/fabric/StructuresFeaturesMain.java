package com.github.galysso.structures_features.platform.fabric;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureRegistry;
import com.github.galysso.structures_features.config.ServerNamesSetsConfig;
import com.github.galysso.structures_features.platform.fabric.helperImpl.NetworkHelperImpl;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.github.galysso.structures_features.util.StructureNaming;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.level.ServerLevel;

public class StructuresFeaturesMain implements ModInitializer {
    private static ServerNamesSetsConfig SERVER_NAMES_SETS_CONFIG;

	@Override
	public void onInitialize() {
		StructuresFeatures.LOGGER.info("Initialization.");
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerAccessor.setServerInstance(server);
            if (SERVER_NAMES_SETS_CONFIG == null) {
                SERVER_NAMES_SETS_CONFIG = new ServerNamesSetsConfig();
                StructureNaming.get(server.overworld());
                StructureNaming.init();
                for (ServerLevel serverWorld : server.getAllLevels()) {
                    StructureRegistry.get(serverWorld);
                }
            }
        });
        ServerWorldEvents.LOAD.register((server, world) -> {
            StructureRegistry.get(world);
        });

        NetworkHelperImpl.init();
	}
}