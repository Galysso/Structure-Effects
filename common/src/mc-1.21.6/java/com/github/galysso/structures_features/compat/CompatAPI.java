package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.util.StructureNaming;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Optional;
import java.util.Set;

public class CompatAPI {
    private static final Codec<StructuresStorage> STRUCTURES_STORAGE_CODEC =
            CompoundTag.CODEC.xmap(
                tag  -> StructuresStorage.fromNbt(tag, RegistryAccess.EMPTY),
                data -> data.save(new CompoundTag(), RegistryAccess.EMPTY)
            );

    public static final SavedDataType<StructuresStorage> STRUCTURES_STORAGE_TYPE =
        new SavedDataType<>(
            StructuresStorage.ID,
            StructuresStorage::new,  // supplier quand aucun fichier n’existe
            STRUCTURES_STORAGE_CODEC,                   // codec sans contexte
            DataFixTypes.LEVEL       // requis dans 1.21.5
        );

    public static StructuresStorage getStructuresStorage(DimensionDataStorage storage) {
        return storage.computeIfAbsent(STRUCTURES_STORAGE_TYPE);
    }

    private static final Codec<StructureNaming> STRUCTURE_NAMING_CODEC =
        CompoundTag.CODEC.xmap(
                tag  -> StructureNaming.fromNbt(tag, RegistryAccess.EMPTY),
                data -> data.save(new CompoundTag(), RegistryAccess.EMPTY)
        );

    public static final SavedDataType<StructureNaming> STRUCTURE_NAMING_TYPE =
        new SavedDataType<>(
            StructureNaming.ID,
            StructureNaming::new,
            STRUCTURE_NAMING_CODEC,
            DataFixTypes.LEVEL
        );

    public static StructureNaming getStructureNaming(DimensionDataStorage storage) {
        return storage.computeIfAbsent(STRUCTURE_NAMING_TYPE);
    }

    public static Registry<Structure> getStructureRegistry(ServerLevel world) {
        return world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
    }

    public static Optional<Long> getLongFromNbt(CompoundTag nbt, String key) {
        return nbt.getLong(key);
    }

    public static Optional<String> getStringFromNbt(CompoundTag nbt, String key) {
        return nbt.getString(key);
    }

    public static Set<String> getKeysSetFromNbt(CompoundTag nbt) {
        return nbt.keySet();
    }

    public static Optional<CompoundTag> getCompoundFromNbt(CompoundTag nbt, String key) {
        return nbt.getCompound(key);
    }

    public static Optional<ListTag> getListFromNbt(CompoundTag nbt, String key, int index) {
        return nbt.getList(key);
    }

    public static Optional<CompoundTag> getCompoundFromNbtList(ListTag nbt, int index) {
        return nbt.getCompound(index);
    }

    public static Optional<String> getStringFromNbtList(ListTag nbt, int index) {
        return nbt.getString(index);
    }

    public static ServerLevel getServerLevelFromServerPlayer(ServerPlayer player) {
        return player.level();
    }
}
