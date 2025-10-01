package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.duck.ServerPlayerDuck;
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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Compat_ServerPlayer {
    public static ServerLevel getServerLevel(ServerPlayer player) {
        return player.serverLevel();
    }

    public static void saveAdditionalData(ServerPlayer player, Object outputObject) {
        if (!(outputObject instanceof CompoundTag compoundTag)) {
            throw new IllegalArgumentException("Expected CompoundTag, got: " + outputObject.getClass());
        }

        CompoundTag outer = new CompoundTag();
        for (Map.Entry<String, Set<Long>> e : ((ServerPlayerDuck) player).getStructuresEffects().entrySet()) {
            ListTag inner = new ListTag();
            for (Long kv : e.getValue()) {
                inner.add(LongTag.valueOf(kv));
            }
            outer.put(e.getKey(), inner);
        }
        compoundTag.put("effectsDeadline", outer);

        compoundTag.putLongArray(
            "structures",
            ((ServerPlayerDuck) player).getStructureObjects().stream().mapToLong(StructureObject::getId).toArray()
        );
    }

    public static void readAdditionalData(ServerPlayer player, Object inputObject) {
        if (!(inputObject instanceof CompoundTag compoundTag)) {
            throw new IllegalArgumentException("Expected CompoundTag, got: " + inputObject.getClass());
        }

        Map<String, Set<Long>> structures_effects = new java.util.HashMap<>();

        Optional<CompoundTag> outerOpt = Compat_NBT.getCompound(compoundTag, "effectsDeadline");
        if (outerOpt.isPresent()) {
            for (String outerKey : Compat_NBT.getKeysSet(outerOpt.get())) {
                Optional<ListTag> idsOpt = Compat_NBT.getList(outerOpt.get(), outerKey, CompoundTag.TAG_LONG);
                if (idsOpt.isEmpty()) continue;

                Set<Long> set = new java.util.HashSet<>();
                for (Tag id : idsOpt.get()) {
                    Optional<Long> idOpt = Compat_NBT.tagToLong(id);
                    if (idOpt.isEmpty()) continue;
                    set.add(idOpt.get());
                }
                structures_effects.put(outerKey, set);
            }
        }
        ((ServerPlayerDuck) player).setStructuresEffects(structures_effects);

        Set<StructureObject> structureObjects = new java.util.HashSet<>();
        Optional<long[]> idsOpt = Compat_NBT.getLongArray(compoundTag, "structures");
        if (idsOpt.isPresent()) {
            for (long id : idsOpt.get()) {
                structureObjects.add(StructuresStorage.getStructureAtId(id));
            }
        }
        ((ServerPlayerDuck) player).setStructureObjects(structureObjects);
    }
}
