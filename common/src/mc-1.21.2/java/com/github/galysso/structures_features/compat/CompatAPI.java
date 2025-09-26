package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Optional;
import java.util.Set;

public class CompatAPI {
    public static final SavedData.Factory<StructuresStorage> STRUCTURES_STORAGE_TYPE = new SavedData.Factory<>(
        StructuresStorage::new,
        StructuresStorage::fromNbt,
        net.minecraft.util.datafix.DataFixTypes.LEVEL
    );

    public static StructuresStorage getStructuresStorage(DimensionDataStorage storage) {
        return storage.computeIfAbsent(STRUCTURES_STORAGE_TYPE, StructuresStorage.ID);
    }

    public static final SavedData.Factory<StructureNaming> STRUCTURE_NAMING_TYPE =  new SavedData.Factory<>(
        StructureNaming::new,
        StructureNaming::fromNbt,
        DataFixTypes.LEVEL
    );

    public static StructureNaming getStructureNaming(DimensionDataStorage storage) {
        return storage.computeIfAbsent(STRUCTURE_NAMING_TYPE, StructureNaming.ID);
    }

    public static Registry<Structure> getStructureRegistry(ServerLevel world) {
        return world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
    }

    public static Optional<Long> getLongFromNbt(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getLong(key));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getStringFromNbt(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getString(key));
        } else {
            return Optional.empty();
        }
    }

    public static Set<String> getKeysSetFromNbt(CompoundTag nbt) {
        return nbt.getAllKeys();
    }

    public static Optional<CompoundTag> getCompoundFromNbt(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getCompound(key));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<ListTag> getListFromNbt(CompoundTag nbt, String key, int index) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getList(key, index));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<CompoundTag> getCompoundFromNbtList(ListTag nbt, int index) {
        if (nbt.size() > index) {
            return Optional.of(nbt.getCompound(index));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getStringFromNbtList(ListTag nbt, int index) {
        if (nbt.size() > index) {
            return Optional.of(nbt.getString(index));
        } else {
            return Optional.empty();
        }
    }

    public static ServerLevel getServerLevelFromServerPlayer(ServerPlayer player) {
        return player.serverLevel();
    }
}
