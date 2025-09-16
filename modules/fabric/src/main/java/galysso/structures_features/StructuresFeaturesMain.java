package galysso.structures_features;

import galysso.structures_features.api.StructureRegistry;
import galysso.structures_features.config.ModConfigs;
import galysso.structures_features.config.ServerNamesSetsConfig;
import galysso.structures_features.helper.NetworkHelperImpl;
import galysso.structures_features.util.ServerAccessor;
import galysso.structures_features.util.StructureNaming;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;

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
                StructureNaming.get(server.getOverworld());
                StructureNaming.init();
                for (ServerWorld serverWorld : server.getWorlds()) {
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