package com.github.galysso.structures_features.compat;

import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.duck.ServerPlayerDuck;
import com.github.galysso.structures_features.util.StructureNaming;
import com.mojang.serialization.Codec;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Compat_ServerPlayer {
    public static ServerLevel getServerLevel(ServerPlayer player) {
        return player.level();
    }

    public static void saveAdditionalData(ServerPlayer player, Object outputObject) {
        if (!(outputObject instanceof ValueOutput valueOutput)) {
            throw new IllegalArgumentException("Expected ValueOutput, got: " + outputObject.getClass());
        }

        // Map<String, Set<Long>> -> Codec<Map<String, List<Long>>>
        var EFFECTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(Codec.LONG));

        Map<String, Set<Long>> structures_effects = ((ServerPlayerDuck) player).getStructuresEffects();
        Map<String, List<Long>> effectsForCodec = new java.util.HashMap<>();
        for (var e : structures_effects.entrySet()) {
            effectsForCodec.put(
                e.getKey(),
                e.getValue().stream().map(Long::longValue).toList()
            );
        }
        Compat_ValueOutput.store(valueOutput, "effectsDeadline", EFFECTS_CODEC, effectsForCodec);

        Set<StructureObject> structure_objects = ((ServerPlayerDuck) player).getStructureObjects();
        var ids = structure_objects.stream()
            .map(s -> s.getId())
            .map(Long::longValue)
            .toList();

        Compat_ValueOutput.store(valueOutput,"structures", Codec.list(Codec.LONG), ids);
    }

    public static void readAdditionalData(ServerPlayer player, Object inputObject) {
        if (!(inputObject instanceof ValueInput valueInput)) {
            throw new IllegalArgumentException("Expected ValueInput, got: " + inputObject.getClass());
        }

        // mêmes Codecs que côté save
        var EFFECTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(Codec.LONG));
        var LONG_LIST     = Codec.list(Codec.LONG);

        // Réinit
        Map<String, Set<Long>> structures_effects = new java.util.HashMap<>();

        // 1) effectsDeadline
        var effectsMap = Compat_ValueInput.read(valueInput, "effectsDeadline", EFFECTS_CODEC).orElse(java.util.Map.of());
        for (var e : effectsMap.entrySet()) {
            java.util.Set<Long> set = new java.util.HashSet<>(e.getValue());
            structures_effects.put(e.getKey(), set);
        }
        ((ServerPlayerDuck) player).setStructuresEffects(structures_effects);

        // 2) structures
        Set<StructureObject> structures = new java.util.HashSet<>();
        var ids = Compat_ValueInput.read(valueInput, "structures", LONG_LIST).orElse(java.util.List.of());
        for (long id : ids) {
            var so = StructuresStorage.getStructureAtId(id);
            if (so != null) structures.add(so);
        }
        ((ServerPlayerDuck) player).setStructureObjects(structures);
    }
}
