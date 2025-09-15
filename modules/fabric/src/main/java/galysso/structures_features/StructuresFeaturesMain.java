package galysso.structures_features;

import galysso.structures_features.api.StructureRegistry;
import galysso.structures_features.config.ModConfigs;
import galysso.structures_features.config.ServerConfig;
import galysso.structures_features.util.NetworkRegistry;
import galysso.structures_features.util.ServerAccessor;
import galysso.structures_features.util.StructureNaming;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class StructuresFeaturesMain implements ModInitializer {
    private static ServerConfig SERVER_CONFIG;

	@Override
	public void onInitialize() {
		StructuresFeatures.LOGGER.info("Initialization.");
        //SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.SERVER);
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerAccessor.setServerInstance(server);
            if (SERVER_CONFIG == null) {
                SERVER_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerConfig::new, RegisterType.SERVER);
                ModConfigs.setServer(SERVER_CONFIG.toData());
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

        NetworkRegistry.init();
	}
}