package galysso.structures_features.mixin;

import galysso.structures_features.api.StructureObject;
import galysso.structures_features.api.StructureRegistry;
import galysso.structures_features.helper.PlatformLoader;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    private ChunkPos chunkPos = null;
    private ServerWorld world = null;
    private BlockPos blockPos = null;
    private Map<Structure, LongSet> structureReferences;
    private Set<StructureObject> structures = new HashSet<StructureObject>();
    private int ticksCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        ticksCounter++;
        if (ticksCounter < 20) return; // every second
        ticksCounter = 0;

        if (!hasMoved(player)) return; // Player did not move, skipping

        updatePosition(player);
        Set<StructureObject> newStructures = StructureRegistry.getOrCreateStructuresAtPos(world, structureReferences, blockPos);

        for (StructureObject structureObject : newStructures) {
            if (!structures.contains(structureObject)) {
                PlatformLoader.sendWelcome(player, structureObject.getName());
            }
        }
        for (StructureObject structureObject : structures) {
            if (!newStructures.contains(structureObject)) {
                PlatformLoader.sendFarewell(player, structureObject.getName());
            }
        }

        structures = newStructures;
    }

    public boolean hasMoved(ServerPlayerEntity player) {
        return world == null || blockPos == null || !world.equals(player.getServerWorld()) || !blockPos.equals(player.getBlockPos());
    }

    public void updatePosition(ServerPlayerEntity player) {
        BlockPos newBlockPos = player.getBlockPos();
        ChunkPos newChunkPos = new ChunkPos(newBlockPos);
        ServerWorld newWorld = player.getServerWorld();

        if (!newChunkPos.equals(chunkPos) || !newWorld.equals(world)) {
            structureReferences = newWorld.getStructureAccessor().getStructureReferences(newBlockPos);
        }

        blockPos = newBlockPos;
        chunkPos = newChunkPos;
        world = newWorld;
    }
}
