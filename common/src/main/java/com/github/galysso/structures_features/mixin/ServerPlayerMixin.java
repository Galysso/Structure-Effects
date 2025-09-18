package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructureRegistry;
import com.github.galysso.structures_features.helper.PlatformLoader;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    private ChunkPos chunkPos = null;
    private ServerLevel world = null;
    private BlockPos blockPos = null;
    private Map<Structure, LongSet> structureReferences;
    private Set<StructureObject> structures = new HashSet<StructureObject>();
    private int ticksCounter = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer)(Object)this;

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

    public boolean hasMoved(ServerPlayer player) {
        return world == null || blockPos == null || !world.equals(player.serverLevel()) || !blockPos.equals(player.getOnPos());
    }

    public void updatePosition(ServerPlayer player) {
        BlockPos newBlockPos = player.getOnPos();
        ChunkPos newChunkPos = new ChunkPos(newBlockPos);
        ServerLevel newWorld = player.serverLevel();

        if (!newChunkPos.equals(chunkPos) || !newWorld.equals(world)) {
            structureReferences = newWorld.structureManager().getAllStructuresAt(newBlockPos);
        }

        blockPos = newBlockPos;
        chunkPos = newChunkPos;
        world = newWorld;
    }
}
