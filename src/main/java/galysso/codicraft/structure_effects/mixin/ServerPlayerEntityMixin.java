package galysso.codicraft.structure_effects.mixin;

import galysso.codicraft.structure_effects.util.Util;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.rmi.registry.Registry;

import static net.minecraft.registry.Registries.STATUS_EFFECT;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Unique
    ChunkPos chunkPos;
    @Unique
    ServerWorld world;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        BlockPos blockPos = player.getBlockPos();
        ChunkPos newChunkPos = new ChunkPos(blockPos);
        ServerWorld newWorld = player.getServerWorld();

        chunkPos = newChunkPos;
        world = newWorld;
        Structure structure = Util.getStructure(world, blockPos);
        if (structure != null) {
            player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                net.minecraft.entity.effect.StatusEffects.SPEED,
                -1, // 6s
                0,      // niveau I
                true,   // ambient (bulles discrètes)
                false,  // pas de particules
                true    // icône
            ));
        } else {
            player.removeStatusEffect(net.minecraft.entity.effect.StatusEffects.SPEED);
        }
    }
}
