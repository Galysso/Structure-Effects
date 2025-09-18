package com.github.galysso.structures_features;

import com.github.galysso.structures_features.api.StructureRegistry;
import com.github.galysso.structures_features.config.ModConfigs;
import com.github.galysso.structures_features.config.ServerNamesSetsConfig;
import com.github.galysso.structures_features.helper.NetworkHelperImpl;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.github.galysso.structures_features.util.StructureNaming;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
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
                SERVER_NAMES_SETS_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerNamesSetsConfig::new, RegisterType.SERVER);
                ModConfigs.setServerNamesSets(SERVER_NAMES_SETS_CONFIG.toData());
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