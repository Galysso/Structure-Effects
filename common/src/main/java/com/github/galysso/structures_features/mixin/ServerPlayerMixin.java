package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.compat.Compat_Registry;
import com.github.galysso.structures_features.compat.Compat_ServerPlayer;
import com.github.galysso.structures_features.config.elements.EffectConfig;
import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import com.github.galysso.structures_features.helper.PlatformLoader;
import com.github.galysso.structures_features.util.ServerAccessor;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    private ChunkPos chunkPos = null;
    private ServerLevel world = null;
    private BlockPos blockPos = null;
    private Map<Structure, LongSet> structureReferences;
    private Set<StructureObject> structures = new HashSet<StructureObject>();
    private int ticksCounter = 0;

    private Map<String, Map<Long, Integer>> effectsDeadline = new java.util.HashMap<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        updateStructures();
    }

    @Unique
    private void updateStructures() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        ticksCounter++;
        if (ticksCounter < 20) return; // every second
        ticksCounter = 0;

        if (!hasMoved()) return; // Player did not move, skipping

        updatePosition();
        Set<StructureObject> newStructures = StructuresStorage.getOrCreateStructuresAtPos(world, structureReferences, blockPos);

        for (StructureObject structureObject : newStructures) {
            if (!structures.contains(structureObject)) {
                PlatformLoader.sendWelcome(serverPlayer, structureObject.getName());
                addEffects(structureObject);
            }
        }
        for (StructureObject structureObject : structures) {
            if (!newStructures.contains(structureObject)) {
                PlatformLoader.sendFarewell(serverPlayer, structureObject.getName());
                removeEffects(structureObject);
            }
        }

        structures = newStructures;
    }

    @Unique
    private boolean hasMoved() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        return world == null || blockPos == null || !world.equals(Compat_ServerPlayer.getServerLevel(serverPlayer)) || !blockPos.equals(serverPlayer.getOnPos());
    }

    @Unique
    private void updatePosition() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        BlockPos newBlockPos = serverPlayer.getOnPos();
        ChunkPos newChunkPos = new ChunkPos(newBlockPos);
        ServerLevel newWorld = Compat_ServerPlayer.getServerLevel(serverPlayer);

        if (!newChunkPos.equals(chunkPos) || !newWorld.equals(world)) {
            structureReferences = newWorld.structureManager().getAllStructuresAt(newBlockPos);
        }

        blockPos = newBlockPos;
        chunkPos = newChunkPos;
        world = newWorld;
    }

    @Unique
    private void addEffects(StructureObject structureObject) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        List<EffectConfig> effects = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
        if (effects == null) return;

        for (var effectConfig : effects) {
            ResourceLocation id = ResourceLocation.tryParse(effectConfig.effectId);
            if (id == null) continue;

            RegistryAccess registryAccess = Compat_ServerPlayer.getServerLevel(serverPlayer).registryAccess();
            Registry<MobEffect> reg = Compat_Registry.getRegistry(world, Registries.MOB_EFFECT);
            ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
            Holder<MobEffect> holder = Compat_Registry.getHolder(reg, key).orElse(null);
            if (holder == null) continue;

            Map<Long, Integer> structuresWithTheEffect = effectsDeadline.computeIfAbsent(effectConfig.effectId, k -> new HashMap<>());

            structuresWithTheEffect.put(
                structureObject.getId(),
                ServerAccessor.getCurrentTick() + effectConfig.duration * 20
            );

            serverPlayer.addEffect(
                new MobEffectInstance(
                    holder,
                    effectConfig.duration * 20,
                    effectConfig.amplifier,
                    effectConfig.ambient,
                    effectConfig.visible,
                    effectConfig.showIcon
                )
            );
        }
    }

    @Unique
    private void removeEffects(StructureObject structureObject) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;


        List<EffectConfig> effects = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
        if (effects == null) return;

        Registry<MobEffect> reg = Compat_Registry.getRegistry(Compat_ServerPlayer.getServerLevel(serverPlayer), Registries.MOB_EFFECT);

        for (var effectConfig : effects) {
            Map<Long, Integer> structuresWithTheEffect = effectsDeadline.get(effectConfig.effectId);
            if (structuresWithTheEffect == null) continue;

            Integer deadline = structuresWithTheEffect.remove(structureObject.getId());
            if (deadline == null) continue;

            if (effectConfig.clearedWhenLeaving) {
                ResourceLocation id = ResourceLocation.tryParse(effectConfig.effectId);
                if (id == null) continue;

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
                Holder<MobEffect> holder = Compat_Registry.getHolder(reg, key).orElse(null);
                if (holder == null) continue;

                MobEffectInstance instance = serverPlayer.getEffect(holder);
                if (instance == null) continue;

                MobEffectInstance rebuilt = removeUniqueEffect(instance, effectConfig, Math.max(0, deadline - ServerAccessor.getCurrentTick()));
                serverPlayer.removeEffect(holder);
                if (rebuilt != null) {
                    serverPlayer.addEffect(rebuilt);
                }
            }
        }
    }

    private MobEffectInstance removeUniqueEffect(MobEffectInstance instance, EffectConfig effectConfig, int remainingTicks) {
        if (instance == null) return null;

        MobEffectInstance nextRebuilt = removeUniqueEffect(((MobEffectInstanceDuck) instance).getHidden(), effectConfig, remainingTicks);

        if (
            instance.getEffect().getRegisteredName().equals(effectConfig.effectId)
            && instance.getDuration() == remainingTicks
            && instance.getAmplifier() == effectConfig.amplifier
            && instance.isVisible() == effectConfig.visible
        ) {
            return nextRebuilt;
        }

        return new MobEffectInstance(
            instance.getEffect(),
            instance.getDuration(),
            instance.getAmplifier(),
            instance.isAmbient(),
            instance.isVisible(),
            instance.showIcon(),
            nextRebuilt
        );
    }
}
