package com.github.galysso.structures_features;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.config.server.ServerEffectsConfig;
import com.github.galysso.structures_features.config.server.ServerIntegrationsConfig;
import com.github.galysso.structures_features.config.server.ServerNamesSetsConfig;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.github.galysso.structures_features.util.StructureNaming;
import me.fzzyhmstrs.fzzy_config.api.ConfigApiJava;
import me.fzzyhmstrs.fzzy_config.api.RegisterType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructuresFeatures {
    private static boolean serverInitialized = false;

    public static final String MOD_ID = "structures_features";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ServerNamesSetsConfig SERVER_NAMES_SETS_CONFIG;
    public static ServerEffectsConfig SERVER_EFFECTS_CONFIG;
    public static ServerIntegrationsConfig SERVER_INTEGRATIONS_CONFIG;

    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(StructuresFeatures.MOD_ID, path);
    }

    public static void serverInit(MinecraftServer server) {
        if (serverInitialized) {
            return;
        }
        serverInitialized = true;

        ServerAccessor.setServerInstance(server);
        if (StructuresFeatures.SERVER_NAMES_SETS_CONFIG == null) {
            StructuresFeatures.SERVER_NAMES_SETS_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerNamesSetsConfig::new, RegisterType.SERVER);
            StructureNaming.get(server.overworld());
            StructureNaming.init();
            for (ServerLevel serverWorld : server.getAllLevels()) {
                StructuresStorage.get(serverWorld);
            }
        }
        if (StructuresFeatures.SERVER_EFFECTS_CONFIG == null) {
            StructuresFeatures.SERVER_EFFECTS_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerEffectsConfig::new, RegisterType.SERVER);
        }
        if (SERVER_INTEGRATIONS_CONFIG == null) {
            SERVER_INTEGRATIONS_CONFIG = ConfigApiJava.registerAndLoadConfig(ServerIntegrationsConfig::new, RegisterType.SERVER);
        }
    }
}
