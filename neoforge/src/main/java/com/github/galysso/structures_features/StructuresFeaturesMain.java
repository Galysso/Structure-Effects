package com.github.galysso.structures_features;

import com.github.galysso.structures_features.api.StructureRegistry;
import com.github.galysso.structures_features.config.ModConfigs;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.github.galysso.structures_features.util.StructureNaming;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.common.Mod;

@Mod(StructuresFeatures.MOD_ID)
public class StructuresFeaturesMain {
    public StructuresFeaturesMain() {
        // Code d’initialisation du mod (éventuellement avec IEventBus, etc.)

        /* WORKING FABRIC CODE */
        /*
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
        */
    }
}