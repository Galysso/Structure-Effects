package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.Optional;
import java.util.Set;

public class Compat_NBT {
    public static Optional<Long> getLong(CompoundTag nbt, String key) {
        return nbt.getLong(key);
    }

    public static Optional<String> getString(CompoundTag nbt, String key) {
        return nbt.getString(key);
    }

    public static Set<String> getKeysSet(CompoundTag nbt) {
        return nbt.keySet();
    }

    public static Optional<CompoundTag> getCompound(CompoundTag nbt, String key) {
        return nbt.getCompound(key);
    }

    public static Optional<ListTag> getList(CompoundTag nbt, String key, int index) {
        return nbt.getList(key);
    }

    public static Optional<CompoundTag> getCompoundFromList(ListTag nbt, int index) {
        return nbt.getCompound(index);
    }

    public static Optional<String> getStringFromList(ListTag nbt, int index) {
        return nbt.getString(index);
    }

}
