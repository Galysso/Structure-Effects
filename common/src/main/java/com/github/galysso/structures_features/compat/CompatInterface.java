package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructuresStorage;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Optional;
import java.util.Set;

public interface CompatInterface {
    static StructuresStorage getStructuresStorage(DimensionDataStorage storage) {
        return null;
    }

    static Registry<Structure> getStructureRegistry(ServerLevel world, ResourceKey<Structure> structure) {
        return null;
    }

    static Optional<Long> getLongFromNbt(CompoundTag nbt, String key) {
        return Optional.empty();
    }

    static Optional<String> getStringFromNbt(CompoundTag nbt, String key) {
        return Optional.empty();
    }

    static Set<String> getKeysSetFromNbt(CompoundTag nbt) {
        return null;
    }

    static Optional<CompoundTag> getCompoundFromNbt(CompoundTag nbt, String key) {
        return Optional.empty();
    }

    static Optional<ListTag> getListFromNbt(CompoundTag nbt, String key, int index) {
        return Optional.empty();
    }

    static Optional<CompoundTag> getCompoundFromNbtList(ListTag nbt, int index) {
        return Optional.empty();
    }

    static Optional<String> getStringFromNbtList(ListTag nbt, int index) {
        return Optional.empty();
    }
}
