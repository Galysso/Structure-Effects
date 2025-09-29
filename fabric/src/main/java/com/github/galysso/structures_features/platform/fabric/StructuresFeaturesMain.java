package com.github.galysso.structures_features.platform.fabric;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.platform.fabric.helper.NetworkHelperImpl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;

public class StructuresFeaturesMain implements ModInitializer {
	@Override
	public void onInitialize() {
		StructuresFeatures.LOGGER.info("Initialization.");
        ServerLifecycleEvents.SERVER_STARTED.register(StructuresFeatures::serverInit);
        ServerWorldEvents.LOAD.register((server, world) -> {
            StructuresStorage.get(world);
        });
        NetworkHelperImpl.init();
	}
}