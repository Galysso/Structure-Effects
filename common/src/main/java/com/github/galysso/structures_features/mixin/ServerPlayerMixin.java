package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.compat.Compat_NBT;
import com.github.galysso.structures_features.compat.Compat_Registry;
import com.github.galysso.structures_features.compat.Compat_ServerPlayer;
import com.github.galysso.structures_features.config.server.elements.EffectConfig;
import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import com.github.galysso.structures_features.helper.PlatformLoader;
import com.github.galysso.structures_features.util.ServerAccessor;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.*;
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
    // Data for tracking structures
    @Unique
    private ChunkPos structures_features$chunkPos = null;
    @Unique
    private ServerLevel structures_features$world = null;
    @Unique
    private BlockPos structures_features$blockPos = null;
    @Unique
    private Map<Structure, LongSet> structures_features$structureReferences;
    @Unique
    private Set<StructureObject> structures_features$structures = new HashSet<StructureObject>();

    // Tick counter to limit structure checks
    @Unique
    private int structures_features$ticksCounter = 0;

    // Data for effects management
    @Unique
    private Map<String, Set<Long>> structures_features$effects = new java.util.HashMap<>();

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        structures_features$updateStructures();
    }

    @Unique
    private void structures_features$updateStructures() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        structures_features$ticksCounter++;
        if (structures_features$ticksCounter < 20) return; // every second
        structures_features$ticksCounter = 0;

        if (!structures_features$hasMoved()) return; // Player did not move, skipping

        structures_features$updatePosition();
        Set<StructureObject> newStructures = StructuresStorage.getOrCreateStructuresAtPos(structures_features$world, structures_features$structureReferences, structures_features$blockPos);

        for (StructureObject structureObject : newStructures) {
            if (!structures_features$structures.contains(structureObject)) {
                PlatformLoader.sendWelcome(serverPlayer, structureObject.getName());
                structures_features$addEffects(structureObject);
            }
        }
        for (StructureObject structureObject : structures_features$structures) {
            if (!newStructures.contains(structureObject)) {
                PlatformLoader.sendFarewell(serverPlayer, structureObject.getName());
                structures_features$removeEffects(structureObject);
            }
        }

        structures_features$structures = newStructures;
    }

    @Unique
    private boolean structures_features$hasMoved() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
        return structures_features$world == null || structures_features$blockPos == null || !structures_features$world.equals(Compat_ServerPlayer.getServerLevel(serverPlayer)) || !structures_features$blockPos.equals(serverPlayer.getOnPos());
    }

    @Unique
    private void structures_features$updatePosition() {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        BlockPos newBlockPos = serverPlayer.getOnPos();
        ChunkPos newChunkPos = new ChunkPos(newBlockPos);
        ServerLevel newWorld = Compat_ServerPlayer.getServerLevel(serverPlayer);

        if (!newChunkPos.equals(structures_features$chunkPos) || !newWorld.equals(structures_features$world)) {
            structures_features$structureReferences = newWorld.structureManager().getAllStructuresAt(newBlockPos);
        }

        structures_features$blockPos = newBlockPos;
        structures_features$chunkPos = newChunkPos;
        structures_features$world = newWorld;
    }

    @Unique
    private void structures_features$addEffects(StructureObject structureObject) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;

        List<EffectConfig> effects = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
        if (effects == null) return;

        for (var effectConfig : effects) {
            ResourceLocation id = ResourceLocation.tryParse(effectConfig.effectId);
            if (id == null) continue;

            RegistryAccess registryAccess = Compat_ServerPlayer.getServerLevel(serverPlayer).registryAccess();
            Registry<MobEffect> reg = Compat_Registry.getRegistry(structures_features$world, Registries.MOB_EFFECT);
            ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
            Holder<MobEffect> holder = Compat_Registry.getHolder(reg, key).orElse(null);
            if (holder == null) continue;

            Set<Long> structuresWithTheEffect = structures_features$effects.computeIfAbsent(effectConfig.effectId, k -> new HashSet<Long>());

            structuresWithTheEffect.add(
                structureObject.getId()
            );

            MobEffectInstance newEffect = new MobEffectInstance(
                holder,
                effectConfig.duration <= 0 ? MobEffectInstance.INFINITE_DURATION : effectConfig.duration * 20,
                effectConfig.amplifier,
                effectConfig.ambient,
                effectConfig.visible,
                effectConfig.showIcon
            );
            ((MobEffectInstanceDuck) newEffect).setResponsibleStructure(structureObject.getId());
            serverPlayer.addEffect(newEffect);
        }
    }

    @Unique
    private void structures_features$removeEffects(StructureObject structureObject) {
        ServerPlayer serverPlayer = (ServerPlayer) (Object) this;


        List<EffectConfig> effects = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
        if (effects == null) return;

        Registry<MobEffect> reg = Compat_Registry.getRegistry(Compat_ServerPlayer.getServerLevel(serverPlayer), Registries.MOB_EFFECT);

        for (var effectConfig : effects) {
            Set<Long> structuresWithTheEffect = structures_features$effects.get(effectConfig.effectId);
            if (structuresWithTheEffect == null) continue;

            structuresWithTheEffect.remove(structureObject.getId());

            if (effectConfig.clearedWhenLeaving) {
                ResourceLocation id = ResourceLocation.tryParse(effectConfig.effectId);
                if (id == null) continue;

                ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);
                Holder<MobEffect> holder = Compat_Registry.getHolder(reg, key).orElse(null);
                if (holder == null) continue;

                MobEffectInstance instance = serverPlayer.getEffect(holder);
                if (instance == null) continue;

                MobEffectInstance rebuilt = structures_features$removeUniqueEffect(instance, structureObject.getId());
                serverPlayer.removeEffect(holder);
                if (rebuilt != null) {
                    serverPlayer.addEffect(rebuilt);
                }
            }
        }
    }

    @Unique
    private MobEffectInstance structures_features$removeUniqueEffect(MobEffectInstance instance, long structureId) {
        if (instance == null) return null;

        MobEffectInstance nextRebuilt = structures_features$removeUniqueEffect(((MobEffectInstanceDuck) instance).getHidden(), structureId);

        if (
            ((MobEffectInstanceDuck) instance).getResponsibleStructure() == structureId
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

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void onSaveAdditionalData(net.minecraft.nbt.CompoundTag compoundTag, CallbackInfo ci) {
        CompoundTag outer = new CompoundTag();
        for (Map.Entry<String, Set<Long>> e : structures_features$effects.entrySet()) {
            ListTag inner = new ListTag();
            for (Long kv : e.getValue()) {
                inner.add(LongTag.valueOf(kv));
            }
            outer.put(e.getKey(), inner);
        }
        compoundTag.put("effectsDeadline", outer);

        ListTag structuresNbt = new ListTag();
        for (var e : this.structures_features$structures) {
            structuresNbt.add(LongTag.valueOf(e.getId()));
        }
        compoundTag.put("structures", structuresNbt);

        compoundTag.putLongArray(
            "structures",
            this.structures_features$structures.stream().mapToLong(StructureObject::getId).toArray()
        );
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void onReadAdditionalData(net.minecraft.nbt.CompoundTag compoundTag, CallbackInfo ci) {
        this.structures_features$effects = new java.util.HashMap<>();

        Optional<CompoundTag> outerOpt = Compat_NBT.getCompound(compoundTag, "effectsDeadline");
        if (outerOpt.isPresent()) {
            for (String outerKey : Compat_NBT.getKeysSet(outerOpt.get())) {
                Optional<ListTag> idsOpt = Compat_NBT.getList(outerOpt.get(), outerKey, CompoundTag.TAG_LONG);
                if (idsOpt.isEmpty()) continue;

                Set<Long> set = new java.util.HashSet<>();
                for (Tag id : idsOpt.get()) {
                    Optional<Long> idOpt = Compat_NBT.tagToLong(id);
                    if (idOpt.isEmpty()) continue;
                    set.add(idOpt.get());
                }
                this.structures_features$effects.put(outerKey, set);
            }
        }

        Optional<long[]> idsOpt = Compat_NBT.getLongArray(compoundTag, "structures");
        if (idsOpt.isPresent()) {
            System.out.println("[" + StructuresFeatures.MOD_ID + "]: " + Arrays.toString(idsOpt.get()));
            for (long id : idsOpt.get()) {
                this.structures_features$structures.add(StructuresStorage.getStructureAtId(id));
            }
        }
        System.out.println("[" + StructuresFeatures.MOD_ID + "]: " + this.structures_features$structures);
    }
}
