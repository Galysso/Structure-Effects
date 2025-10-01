package com.github.galysso.structures_features.api;

import com.github.galysso.structures_features.compat.Compat_NBT;
import com.github.galysso.structures_features.compat.Compat_Registry;
import com.github.galysso.structures_features.compat.Compat_SavedData;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.github.galysso.structures_features.StructuresFeatures.MOD_ID;

public class StructuresStorage extends SavedData {
    private record InstanceKey(long startChunk, String structureId) { }

    private boolean isOverworld;
    private static long counter;
    private Map<InstanceKey, StructureObject> structuresMap;
    static private Map<Long, StructureObject> structuresById = new HashMap<>();

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
    public static StructureObject getStructureAtId(long id) {
        return structuresById.get(id);
    }

    @Nullable
    public static StructureObject getOrCreateStructureObject(ServerLevel world, BlockPos pos, Structure structure) {
        StructureManager structureAccessor = world.structureManager();
        long structureChunkPos = structureAccessor.getStructureAt(pos, structure).getChunkPos().toLong();

        Map<InstanceKey, StructureObject> structuresAtDimension = get(world).structuresMap;

        Registry<Structure> structures = Compat_Registry.getRegistry(world, Registries.STRUCTURE);
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
            StructuresStorage.get(world).setDirty();
        }

        return structureObject;
    }

    public long getCounter() {
        return counter;
    }

    /* ----- persistent state ----- */
    public static final String ID = "structures_features_structure_registry";
    public StructuresStorage() {
        counter = 0;
        isOverworld = false;
        structuresMap = new HashMap<>();
    }

    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        if (isOverworld) {
            nbt.putLong("counter", counter);
        }

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

    public static StructuresStorage fromNbt(CompoundTag nbt, HolderLookup.Provider lookup) {
        System.out.println("[" + MOD_ID + "] Loading StructuresStorage from NBT");
        StructuresStorage structuresStorage = new StructuresStorage();
        if (structuresStorage.isOverworld) {
            Optional<Long> counterOpt = Compat_NBT.getLong(nbt, "counter");
            counterOpt.ifPresent(aLong -> structuresStorage.counter = aLong);
        }

        Optional<ListTag> entries = Compat_NBT.getList(nbt, "structures", CompoundTag.TAG_COMPOUND);
        if (entries.isEmpty()) return structuresStorage;

        for (int i = 0; i < entries.get().size(); i++) {
            Optional<CompoundTag> compound = Compat_NBT.getCompoundFromList(entries.get(), i);
            if (compound.isEmpty()) continue;

            Optional<Long> startChunkOpt = Compat_NBT.getLong(compound.get(), "start_chunk");
            Optional<String> structureIdOpt = Compat_NBT.getString(compound.get(), "structure_id");
            if (startChunkOpt.isEmpty() || structureIdOpt.isEmpty()) continue;

            InstanceKey k = new InstanceKey(
                startChunkOpt.get(),
                structureIdOpt.get()
            );

            Optional<CompoundTag> dataOpt = Compat_NBT.getCompound(compound.get(), "data");
            if (dataOpt.isEmpty()) continue;

            StructureObject structureObject = StructureObject.fromNbt(dataOpt.get());
            structuresStorage.structuresMap.put(k, structureObject);
            structuresById.put(structureObject.getId(), structureObject);
        }
        return structuresStorage;
    }

    private void setLevel(ServerLevel level) {
        this.isOverworld = level.dimension() == Level.OVERWORLD;
    }

    public static StructuresStorage get(ServerLevel level) {
        StructuresStorage storage = Compat_SavedData.getStructuresStorage(level.getDataStorage());
        storage.setLevel(level);
        return storage;
    }
}
