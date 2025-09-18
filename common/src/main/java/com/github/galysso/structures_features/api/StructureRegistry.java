package com.github.galysso.structures_features.api;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.galysso.structures_features.StructuresFeatures.MOD_ID;

public class StructureRegistry extends SavedData {
    private record InstanceKey(long startChunk, String structureId) { }

    private long counter;
    private Map<InstanceKey, StructureObject> structuresMap;

    // Cached player information
    private record PlayerData(BlockPos pos, Map<Long, StructureObject> structures) { }
    private Map<UUID, PlayerData> playerDataCache = new HashMap<UUID, PlayerData>();

    public static Set<StructureObject> getOrCreateStructuresAtPos(ServerLevel world, Map<Structure, LongSet> structureReferences, BlockPos pos) {
        Set<StructureObject> structures = new HashSet<>();

        if (structureReferences == null) return structures;

        for (Structure structure : structureReferences.keySet()) {
            StructureStart structureStart = world.structureManager().getStructureAt(pos, structure);

            if (structureStart != StructureStart.INVALID_START) {
                if (structureStart.getBoundingBox().isInside(pos)) {
                    StructureObject structureObject = getOrCreateStructureObject(world, pos, structure);
                    structures.add(structureObject);
                }
            }
        }
        return structures;
    }

    @Nullable
    public static StructureObject getOrCreateStructureObject(ServerLevel world, BlockPos pos, Structure structure) {
        StructureManager structureAccessor = world.structureManager();
        long structureChunkPos = structureAccessor.getStructureAt(pos, structure).getChunkPos().toLong();

        Map<InstanceKey, StructureObject> structuresAtDimension = get(world).structuresMap;
        //String structureId = world.getRegistryManager().get(Registries.STRUCTURE).getId(structure).toString();
        //TODO: CA cloche ici

        Registry<Structure> structures = world.registryAccess().registryOrThrow(Registries.STRUCTURE);
        ResourceLocation structureIdentifier = structures.getKey(structure);
        if (structureIdentifier == null) {
            System.err.println("[" + MOD_ID + "] Could not find structure: " + structure);
            return null; // Structure not registered, should not happen
        }
        String structureIdAsString = structureIdentifier.toString();

        StructureObject structureObject = structuresAtDimension.get(
            new InstanceKey(
                structureChunkPos,
                structureIdAsString
            )
        );

        if (structureObject == null) {
            structureObject = StructureObject.create(get(world).counter++, structureIdentifier);
            structuresAtDimension.put(
                new InstanceKey(
                    structureChunkPos,
                    structureIdAsString
                ),
                structureObject
            );
            StructureRegistry.get(world).setDirty();
        }

        return structureObject;
    }


    /* ----- persistent state ----- */
    public static final String ID = "structures_features_structure_registry";
    public StructureRegistry() {
        counter = 0;
        structuresMap = new HashMap<>();
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.put("counter", LongTag.valueOf(counter));

        ListTag entries = new ListTag();
        for (var structureEntry : structuresMap.entrySet()) {
            InstanceKey key = structureEntry.getKey();
            StructureObject structureObject = structureEntry.getValue();

            CompoundTag compound = new CompoundTag();
            compound.putLong("start_chunk", key.startChunk());
            compound.putString("structure_id", key.structureId());

            CompoundTag data = new CompoundTag();
            structureObject.writeNbt(data);
            compound.put("data", data);

            entries.add(compound);
        }
        nbt.put("structures", entries);
        return nbt;
    }

    public static StructureRegistry fromNbt(CompoundTag nbt, HolderLookup.Provider lookup) {
        StructureRegistry structureRegistry = new StructureRegistry();
        if (nbt.contains("counter")) {
            structureRegistry.counter = nbt.getLong("counter");
        }
        ListTag entries = nbt.getList("structures", CompoundTag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag compound = entries.getCompound(i);
            InstanceKey k = new InstanceKey(
                    compound.getLong("start_chunk"),
                    compound.getString("structure_id")
            );
            StructureObject structureObject = StructureObject.fromNbt(compound.getCompound("data"));
            structureRegistry.structuresMap.put(k, structureObject);
        }
        return structureRegistry;
    }

    public static final SavedData.Factory<StructureRegistry> TYPE =
        new SavedData.Factory<>(
            StructureRegistry::new,
            StructureRegistry::fromNbt,
            net.minecraft.util.datafix.DataFixTypes.LEVEL // Mojmap path du DataFixTypes
        );

    public static StructureRegistry get(ServerLevel level) {
        var storage = level.getDataStorage();
        return storage.computeIfAbsent(TYPE, ID);
    }
}
