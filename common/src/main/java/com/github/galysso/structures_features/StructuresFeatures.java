package com.github.galysso.structures_features;

import com.github.galysso.structures_features.config.ModConfigs;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructuresFeatures {
    public static final String MOD_ID = "structures_features";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static ModConfigs CONFIGS;


    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(StructuresFeatures.MOD_ID, path);
    }
}
