package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.compat.*;
import com.github.galysso.structures_features.config.server.elements.EffectConfig;
import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import com.github.galysso.structures_features.duck.ServerPlayerDuck;
import com.github.galysso.structures_features.helper.PlatformLoader;
import com.github.galysso.structures_features.util.ServerAccessor;
import com.mojang.serialization.Codec;
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
//import net.minecraft.world.level.storage.ValueInput;
//import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerDuck {
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

    @Inject(
        method = "addAdditionalSaveData",
        at = @At("TAIL"),
        require = 1
    )
    private void onSaveAdditionalData(@Coerce Object outputObject, CallbackInfo ci) {
        Compat_ServerPlayer.saveAdditionalData((ServerPlayer) (Object) this, outputObject);
    }

    @Inject(
        method = "readAdditionalSaveData",
        at = @At("TAIL"),
        require = 1
    )
    private void onReadAdditionalData(@Coerce Object inputObject, CallbackInfo ci) {
        Compat_ServerPlayer.readAdditionalData((ServerPlayer) (Object) this, inputObject);
    }

    @Override
    public void setStructuresEffects(Map<String, Set<Long>> structures_features$effects) {
        this.structures_features$effects = structures_features$effects;
    }

    @Override
    public void setStructureObjects(Set<StructureObject> structures_features$structures) {
        this.structures_features$structures = structures_features$structures;
    }

    @Override
    public Map<String, Set<Long>> getStructuresEffects() {
        return this.structures_features$effects;
    }

    @Override
    public Set<StructureObject> getStructureObjects() {
        return this.structures_features$structures;
    }

    // ----- 1.21.6+ -----
    /*@Group(name = "save_data", min = 1, max = 2)
    @Inject(
        method = "addAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueOutput;)V",
        at = @At("TAIL"),
        require = 0
    )
    private void onSaveAdditionalData(@Coerce Object valueOutput, CallbackInfo ci) {
        // Map<String, Set<Long>> -> Codec<Map<String, List<Long>>>
        var EFFECTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(Codec.LONG));

        // 1) effectsDeadline : map<string, list<long>>
        java.util.Map<String, java.util.List<Long>> effectsForCodec = new java.util.HashMap<>();
        for (var e : structures_features$effects.entrySet()) {
            effectsForCodec.put(
                    e.getKey(),
                    e.getValue().stream().map(Long::longValue).toList()
            );
        }
        Compat_ValueOutput.store(valueOutput, "effectsDeadline", EFFECTS_CODEC, effectsForCodec);

        // 2) structures : list<long> (ids)
        var ids = structures_features$structures.stream()
                .map(s -> s.getId())
                .map(Long::longValue)
                .toList();

        Compat_ValueOutput.store(valueOutput,"structures", Codec.list(Codec.LONG), ids);
    }

    @Group(name = "load_data", min = 1, max = 2)
    @Inject(
        method = "readAdditionalSaveData(Lnet/minecraft/world/level/storage/ValueInput;)V",
        at = @At("TAIL"),
        require = 0
    )
    private void onReadAdditionalData(@Coerce Object valueInput, CallbackInfo ci) {
        // mêmes Codecs que côté save
        var EFFECTS_CODEC = Codec.unboundedMap(Codec.STRING, Codec.list(Codec.LONG));
        var LONG_LIST     = Codec.list(Codec.LONG);

        // Réinit
        this.structures_features$effects = new java.util.HashMap<>();

        // 1) effectsDeadline
        var effectsMap = Compat_ValueInput.read(valueInput, "effectsDeadline", EFFECTS_CODEC).orElse(java.util.Map.of());
        for (var e : effectsMap.entrySet()) {
            java.util.Set<Long> set = new java.util.HashSet<>(e.getValue());
            this.structures_features$effects.put(e.getKey(), set);
        }

        // 2) structures
        var ids = Compat_ValueInput.read(valueInput, "structures", LONG_LIST).orElse(java.util.List.of());
        for (long id : ids) {
            var so = StructuresStorage.getStructureAtId(id);
            if (so != null) this.structures_features$structures.add(so);
        }
    }*/
}
