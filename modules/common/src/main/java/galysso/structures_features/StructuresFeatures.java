package galysso.structures_features;

import galysso.structures_features.config.ModConfigs;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructuresFeatures {
    public static final String MOD_ID = "structures_features";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfigs CONFIGS;


    public static Identifier identifier(String path) {
        return Identifier.of(StructuresFeatures.MOD_ID, path);
    }
}
