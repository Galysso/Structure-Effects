package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
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
        if (nbt.contains(key)) {
            return Optional.of(nbt.getLong(key));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getString(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getString(key));
        } else {
            return Optional.empty();
        }
    }

    public static Set<String> getKeysSet(CompoundTag nbt) {
        return nbt.getAllKeys();
    }

    public static Optional<CompoundTag> getCompound(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getCompound(key));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<ListTag> getList(CompoundTag nbt, String key, int index) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getList(key, index));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<CompoundTag> getCompoundFromList(ListTag nbt, int index) {
        if (nbt.size() > index) {
            return Optional.of(nbt.getCompound(index));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<String> getStringFromList(ListTag nbt, int index) {
        if (nbt.size() > index) {
            return Optional.of(nbt.getString(index));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<long[]> getLongArray(CompoundTag nbt, String key) {
        if (nbt.contains(key)) {
            return Optional.of(nbt.getLongArray(key));
        } else {
            return Optional.empty();
        }
    }

    public static Optional<Long> tagToLong(Tag tag) {
        if (tag instanceof LongTag) {
            return Optional.of(((net.minecraft.nbt.LongTag) tag).getAsLong());
        } else {
            return Optional.empty();
        }
    }
}
