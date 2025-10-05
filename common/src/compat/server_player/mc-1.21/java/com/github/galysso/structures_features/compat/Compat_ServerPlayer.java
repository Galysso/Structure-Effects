package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.duck.ServerPlayerDuck;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
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

        // Effects currently applied to the player
        CompoundTag outer = new CompoundTag();
        for (var e : ((ServerPlayerDuck) player).getStructuresEffects().entrySet()) {
            ListTag listTag = new ListTag();
            for (String value : e.getValue()) {
                if (value != null) {
                    listTag.add(StringTag.valueOf(value));   // <-- liste de strings
                }
            }
            outer.put(e.getKey().toString(), listTag);          // <-- tableau de longs
        }
        compoundTag.put("structures_effects", outer);

        System.out.println("Saving structures: " + outer);

        // Structures at the current player location
        compoundTag.putLongArray(
            "structures",
            ((ServerPlayerDuck) player).getStructureObjects().stream().mapToLong(StructureObject::getId).toArray()
        );
    }

    public static void readAdditionalData(ServerPlayer player, Object inputObject) {
        if (!(inputObject instanceof CompoundTag compoundTag)) {
            throw new IllegalArgumentException("Expected CompoundTag, got: " + inputObject.getClass());
        }

        Map<Long, Set<String>> structures_effects = new java.util.HashMap<>();

        // Effects applied to the player (before logging out)
        Optional<CompoundTag> outerOpt = Compat_NBT.getCompound(compoundTag, "structures_effects");
        if (outerOpt.isPresent()) {
            for (String outerKey : Compat_NBT.getKeysSet(outerOpt.get())) {
                Optional<ListTag> idsOpt = Compat_NBT.getList(outerOpt.get(), outerKey, Tag.TAG_STRING);
                if (idsOpt.isEmpty()) continue;

                Set<String> set = new java.util.HashSet<>();
                for (int i = 0; i < idsOpt.get().size(); i++) {
                    Optional<String> idOpt = Compat_NBT.getStringFromList(idsOpt.get(), i);
                    idOpt.ifPresent(set::add);
                }
                structures_effects.put(Long.parseLong(outerKey), set);
            }
        }
        ((ServerPlayerDuck) player).setStructuresEffects(structures_effects);

        // Structures at the player location (before logging out)
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
