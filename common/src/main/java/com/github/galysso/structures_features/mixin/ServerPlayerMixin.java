package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.compat.*;
import com.github.galysso.structures_features.config.server.elements.EffectConfig;
import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import com.github.galysso.structures_features.duck.ServerPlayerDuck;
import com.github.galysso.structures_features.helper.PlatformLoader;
import com.github.galysso.structures_features.util.EffectUtil;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerDuck {
    // Lazy data cleaning
    private boolean structures_features$cleaned = false;

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
    private Set<StructureObject> structures_features$structures;

    // Tick counter to limit structure checks
    @Unique
    private int structures_features$ticksCounter = 0;

    // Data for effects management
    @Unique
    private Map<Long, Set<String>> structures_features$effects;

     @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        structures_features$structureReferences = Map.of();
        structures_features$effects = new HashMap<>();
        structures_features$structures = new HashSet<>();
     }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        structures_features$cleanData();
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

        Map<String, ? extends EffectConfig> effects = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
        if (effects == null) return;

        for (var entryEffectConfig : effects.entrySet()) {
            String effectId = entryEffectConfig.getKey();
            EffectConfig effectConfig = entryEffectConfig.getValue();

            Optional<Holder<MobEffect>> holderOpt = EffectUtil.getMobEffectHolder(effectId);
            if (holderOpt.isEmpty()) continue;

            Set<String> effectsForTheStructure = structures_features$effects.computeIfAbsent(structureObject.getId(), k -> new HashSet<String>());

            effectsForTheStructure.add(effectId);

            MobEffectInstance newEffect = new MobEffectInstance(
                holderOpt.get(),
                effectConfig.duration < 0 ? MobEffectInstance.INFINITE_DURATION : effectConfig.duration * 20,
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


        Map<String, ? extends EffectConfig> effectConfigs = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());

        Set<String> effectsAssociatedWithTheStructure = structures_features$effects.get(structureObject.getId());

        for (var innerIt = effectsAssociatedWithTheStructure.iterator(); innerIt.hasNext(); ) {
            String effectId = innerIt.next();
            EffectConfig effectConfig = effectConfigs == null ? null : effectConfigs.get(effectId);

            if (effectConfig == null || effectConfig.clearedWhenLeaving) {
                Optional<Holder<MobEffect>> holderOpt = EffectUtil.getMobEffectHolder(effectId);
                if (holderOpt.isEmpty()) continue;

                Optional<MobEffectInstance> instance = EffectUtil.getMobEffectInstance(serverPlayer, holderOpt.get()); //serverPlayer.getEffect(holder);
                if (instance.isEmpty()) continue;

                MobEffectInstance rebuilt = structures_features$removeUniqueEffect(instance.get(), structureObject.getId());
                serverPlayer.removeEffect(holderOpt.get());
                if (rebuilt != null) {
                    serverPlayer.addEffect(rebuilt);
                }

                innerIt.remove();
            }
        }
        if (effectsAssociatedWithTheStructure.isEmpty()) {
            structures_features$effects.remove(structureObject.getId());
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

    @Unique
    public void structures_features$cleanData() {
        if (structures_features$cleaned) return;
        structures_features$cleaned = true;

        // Remove effects which do not have a valid config for the responsible structure anymore, or reduce the time if the config changed for a shorter duration
        var outerIt = structures_features$effects.entrySet().iterator();
        while (outerIt.hasNext()) {
            var entryStructuresEffects = outerIt.next();
            long structureId = entryStructuresEffects.getKey();
            Set<String> effectIds = entryStructuresEffects.getValue();
            StructureObject structureObject = StructuresStorage.getStructureAtId(structureId);
            for (var innerIt = effectIds.iterator(); innerIt.hasNext(); ) {
                String effectId = innerIt.next();

                Optional<Holder<MobEffect>> holderOpt = EffectUtil.getMobEffectHolder(effectId);
                if (holderOpt.isEmpty()) {
                    innerIt.remove();
                    continue;
                }

                ServerPlayer serverPlayer = (ServerPlayer) (Object) this;
                Optional<MobEffectInstance> instanceOpt = EffectUtil.getMobEffectInstance(serverPlayer, holderOpt.get());
                if (instanceOpt.isEmpty()) {
                    innerIt.remove();
                    continue;
                }

                Map<String, ? extends EffectConfig> effectConfigs = StructuresFeatures.SERVER_EFFECTS_CONFIG.structuresEffects.get(structureObject.getStructureId().toString());
                EffectConfig effectConfig = effectConfigs == null ? null : effectConfigs.get(effectId);

                System.out.println("Structures: " + structures_features$structures);

                if (
                    structureObject == null // structure somehow does not exist anymore
                        || effectConfig == null // structure does not have any effect config anymore
                        || ( // effect is configured to be removed when leaving, but player is not in the structure anymore
                            effectConfig.clearedWhenLeaving
                            && !structures_features$structures.contains(structureObject)
                        )
                ) {
                    innerIt.remove();
                    MobEffectInstance rebuilt = structures_features$removeUniqueEffect(instanceOpt.get(), structureId);
                    serverPlayer.removeEffect(holderOpt.get());
                    if (rebuilt != null) {
                        serverPlayer.addEffect(rebuilt);
                    }
                } else {
                    MobEffectInstance instanceIter = instanceOpt.get();
                    while (
                        instanceIter != null && ((MobEffectInstanceDuck) instanceIter).getResponsibleStructure() != structureId
                    ) {
                        instanceIter = ((MobEffectInstanceDuck) instanceIter).getHidden();
                    }

                    if (instanceIter != null && ((MobEffectInstanceDuck) instanceIter).getResponsibleStructure() == structureId) {
                        boolean changeAmplifier = instanceIter.getAmplifier() != effectConfig.amplifier;
                        boolean changeDuration = (!instanceIter.isInfiniteDuration() && !effectConfig.isInfiniteDuration() && instanceIter.getDuration() > effectConfig.duration * 20) || (instanceIter.isInfiniteDuration() != effectConfig.isInfiniteDuration());
                        boolean changeAmbient = instanceIter.isAmbient() != effectConfig.ambient;
                        boolean changeVisible = instanceIter.isVisible() != effectConfig.visible;
                        boolean changeIcon = instanceIter.showIcon() != effectConfig.showIcon;

                        if (!changeAmplifier && !changeDuration && !changeAmbient && !changeVisible && !changeIcon) {
                            continue;
                        }

                        structures_features$removeUniqueEffect(instanceOpt.get(), structureId);
                        MobEffectInstance rebuilt = new MobEffectInstance(
                            instanceIter.getEffect(),
                            changeDuration ? (effectConfig.isInfiniteDuration() ? MobEffectInstance.INFINITE_DURATION : effectConfig.duration * 20) : instanceIter.getDuration(),
                            changeAmplifier ? effectConfig.amplifier : instanceIter.getAmplifier(),
                            changeAmbient ? effectConfig.ambient : instanceIter.isAmbient(),
                            changeVisible ? effectConfig.visible : instanceIter.isVisible(),
                            changeIcon ? effectConfig.showIcon : instanceIter.showIcon(),
                            ((MobEffectInstanceDuck) instanceIter).getHidden()
                        );
                        ((MobEffectInstanceDuck) rebuilt).setResponsibleStructure(structureId);
                        serverPlayer.removeEffect(holderOpt.get());
                        serverPlayer.addEffect(rebuilt);
                    }
                }

                if (effectIds.isEmpty()) {
                    outerIt.remove();
                }
            }
        }
    }

    @Override
    public void setStructuresEffects(Map<Long, Set<String>> structures_features$effects) {
        System.out.println("Set structures effects to: " + structures_features$effects);
        this.structures_features$effects = structures_features$effects;
    }

    @Override
    public void setStructureObjects(Set<StructureObject> structures_features$structures) {
        this.structures_features$structures = structures_features$structures;
    }

    @Override
    public Map<Long, Set<String>> getStructuresEffects() {
        return this.structures_features$effects;
    }

    @Override
    public Set<StructureObject> getStructureObjects() {
        return this.structures_features$structures;
    }
}
