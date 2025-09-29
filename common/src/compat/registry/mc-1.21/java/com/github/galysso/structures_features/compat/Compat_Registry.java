package com.github.galysso.structures_features.compat;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

public class Compat_Registry {
    public static Registry getRegistry(ServerLevel world, ResourceKey registryKey) {
        return world.registryAccess().registryOrThrow(registryKey);
    }

    public static Optional<Holder> getHolder(Registry registry, ResourceKey key) {
        return registry.getHolder(key);
    }
}
