package galysso.structures_features.api;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.*;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StructureRegistry extends PersistentState {
    private record InstanceKey(long startChunk, String structureId) { }

    private long counter;
    private Map<InstanceKey, StructureObject> structuresMap;

    // Cached player information
    private record PlayerData(BlockPos pos, Map<Long, StructureObject> structures) { }
    private Map<UUID, PlayerData> playerDataCache = new HashMap<UUID, PlayerData>();

    public static Set<StructureObject> getOrCreateStructuresAtPos(ServerWorld world, Map<Structure, LongSet> structureReferences, BlockPos pos) {
        Set<StructureObject> structures = new HashSet<>();

        if (structureReferences == null) return structures;

        for (Structure structure : structureReferences.keySet()) {
            StructureStart structureStart = world.getStructureAccessor().getStructureAt(pos, structure);

            if (structureStart != StructureStart.DEFAULT) {
                if (structureStart.getBoundingBox().contains(pos)) {
                    StructureObject structureObject = getOrCreateStructureObject(world, pos, structure);
                    structures.add(structureObject);
                }
            }
        }
        return structures;
    }

    @Nullable
    public static StructureObject getOrCreateStructureObject(ServerWorld world, BlockPos pos, Structure structure) {
        StructureAccessor structureAccessor = world.getStructureAccessor();
        StructureStart structureStart = structureAccessor.getStructureAt(pos, structure);
        long structureChunkPos = new ChunkPos(structureStart.getPos().getStartPos()).toLong();

        Map<InstanceKey, StructureObject> structuresAtDimension = get(world).structuresMap;
        StructureObject structureObject = structuresAtDimension.get(
            new InstanceKey(
                structureChunkPos,
                world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure).toString()
            )
        );

        if (structureObject == null) {
            structureObject = StructureObject.create(get(world).counter++, world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure));
            structuresAtDimension.put(
                new InstanceKey(
                    structureChunkPos,
                    world.getRegistryManager().get(RegistryKeys.STRUCTURE).getId(structure).toString()
                ),
                structureObject
            );
            StructureRegistry.get(world).markDirty();
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
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("counter", NbtLong.of(counter));
        NbtList entries = new NbtList();
        for (var structureEntry : structuresMap.entrySet()) {
            InstanceKey key = structureEntry.getKey();
            StructureObject structureObject = structureEntry.getValue();

            NbtCompound compound = new NbtCompound();
            compound.putLong("start_chunk", key.startChunk());
            compound.putString("structure_id", key.structureId());

            NbtCompound data = new NbtCompound();
            structureObject.writeNbt(data);
            compound.put("data", data);

            entries.add(compound);
        }
        nbt.put("structures", entries);
        return nbt;
    }

    public static StructureRegistry fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        StructureRegistry structureRegistry = new StructureRegistry();
        if (nbt.contains("counter")) {
            structureRegistry.counter = nbt.getLong("counter");
        }
        NbtList entries = nbt.getList("structures", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < entries.size(); i++) {
            NbtCompound compound = entries.getCompound(i);
            InstanceKey k = new InstanceKey(
                    compound.getLong("start_chunk"),
                    compound.getString("structure_id")
            );
            StructureObject structureObject = StructureObject.fromNbt(compound.getCompound("data"));
            structureRegistry.structuresMap.put(k, structureObject);
        }
        return structureRegistry;
    }

    public static final PersistentState.Type<StructureRegistry> TYPE =
        new StructureRegistry.Type<>(
            StructureRegistry::new,
            StructureRegistry::fromNbt,
            net.minecraft.datafixer.DataFixTypes.LEVEL
        );

    public static StructureRegistry get(ServerWorld anyWorld) {
        var mgr = anyWorld.getPersistentStateManager();
        return mgr.getOrCreate(TYPE, ID);
    }
}
