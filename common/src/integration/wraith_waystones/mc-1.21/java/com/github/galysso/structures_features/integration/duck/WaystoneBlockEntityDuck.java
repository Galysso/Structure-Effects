package com.github.galysso.structures_features.integration.duck;


import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface WaystoneBlockEntityDuck {
    void structures_features$initializeRegionName(ServerLevel world, BlockPos pos);
}
