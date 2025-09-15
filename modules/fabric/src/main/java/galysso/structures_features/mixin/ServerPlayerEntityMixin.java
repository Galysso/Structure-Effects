package galysso.structures_features.mixin;

import galysso.structures_features.api.StructureObject;
import galysso.structures_features.api.StructureRegistry;
import galysso.structures_features.network.StructureNamePayload;
import galysso.structures_features.util.NetworkUtil;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    ChunkPos chunkPos = null;
    @Unique
    ServerWorld world = null;
    @Unique
    BlockPos blockPos = null;
    @Unique
    Map<Structure, LongSet> structureReferences;
    @Unique
    StructureObject structure = null; // TODO: consider multiple structures overlapping

    private int ticksCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ticksCounter++;

        if (ticksCounter < 20) return; // check structures every second only

        ticksCounter = 0;
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;

        // Catch the new player position
        BlockPos newBlockPos = player.getBlockPos();
        ChunkPos newChunkPos = new ChunkPos(newBlockPos);
        ServerWorld newWorld = player.getServerWorld();
        StructureObject newStructure = null;

        if (newBlockPos.equals(blockPos) && newWorld.equals(world)) {
            // Player did not move, skipping
            return;
        }

        if (!newChunkPos.equals(chunkPos) || !newWorld.equals(world)) {
            // The player changed chunk or dimension, we need to get a new structure references
            structureReferences = newWorld.getStructureAccessor().getStructureReferences(newBlockPos);
        }

        // Update the player position
        blockPos = player.getBlockPos();
        chunkPos = newChunkPos;
        world = newWorld;

        // Get the list of structures at the player position
        List<StructureObject> structures = StructureRegistry.getOrCreateStructuresAtPos(world, structureReferences, blockPos);

        /* // This piece of code can be useful for implementing specific structure effects (to be moved directly within StructureObject)
        System.out.println("Player is in structures: [");
        for (StructureObject structureObject : structures) {
            System.out.println("- " + structureObject.getStructureId().toString() + " (" + structureObject.getName() + ")");
            if (structureObject.getName().equals("minecraft:village_plains")) {
                player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                    net.minecraft.entity.effect.StatusEffects.SPEED,
                    200, // 10s
                    0,      // niveau I
                    true,   // ambient (bulles discrètes)
                    false,  // pas de particules
                    true    // icône
                ));
            }
        }
        System.out.println("]");*/


        if (!structures.isEmpty()) {
            newStructure = structures.getFirst(); // Only consider the first structure for now
        }

        if (structure == null) { // Player was not in a structure before
            if (newStructure != null) { // Player is in a structure now
                if (newStructure.hasName()) { // Send structure name to client (if it has one)
                    ServerPlayNetworking.send(player, new StructureNamePayload(newStructure.getName(), true));
                }
            }
        } else if (newStructure == null) { // Player was in a structure before, but not anymore
            if (structure.hasName()) { // Send recently left structure name to client (if it has one)
                ServerPlayNetworking.send(player, new StructureNamePayload(structure.getName(), false));
            }
        }

        // Update the current structure
        structure = newStructure;
    }
}
