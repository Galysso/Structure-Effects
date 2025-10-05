package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.compat.Compat_NBT;
import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceDuck {
    @Shadow
    private MobEffectInstance hiddenEffect;
    @Unique
    private long responsibleStructure = -1;
    @Shadow
    @Final
    @Mutable
    public static Codec<MobEffectInstance> CODEC;

    @Override
    public MobEffectInstance getHidden() {
        return this.hiddenEffect;
    }

    @Override
    public void setHidden(MobEffectInstance instance) {
        this.hiddenEffect = instance;
    }

    @Override
    public void setResponsibleStructure(long structureId) {
        this.responsibleStructure = structureId;
    }

    @Override
    public long getResponsibleStructure() {
        return this.responsibleStructure;
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void sf$wrapCodec(CallbackInfo ci) {
        final var base = CODEC;
        CODEC = new com.mojang.serialization.Codec<>() {
            @Override
            public <T> com.mojang.serialization.DataResult<
                    com.mojang.datafixers.util.Pair<net.minecraft.world.effect.MobEffectInstance, T>>
            decode(com.mojang.serialization.DynamicOps<T> ops, T input) {

                Optional<Long> marker = Optional.empty();
                if (input instanceof net.minecraft.nbt.Tag t && t instanceof net.minecraft.nbt.CompoundTag ct) {
                    marker = Compat_NBT.getLong(ct, "responsibleStructure");
                }

                if (marker.isPresent()) {
                    final long finalMarker = marker.get();
                    return base.decode(ops, input).map(p -> {
                        ((com.github.galysso.structures_features.duck.MobEffectInstanceDuck) (Object) p.getFirst())
                                .setResponsibleStructure(finalMarker);
                        return p;
                    });
                }

                return base.decode(ops, input);
            }

            @Override
            public <T> com.mojang.serialization.DataResult<T>
            encode(net.minecraft.world.effect.MobEffectInstance value,
                   com.mojang.serialization.DynamicOps<T> ops, T prefix) {
                return base.encode(value, ops, prefix).map(out -> {
                    if (out instanceof net.minecraft.nbt.Tag t && t instanceof net.minecraft.nbt.CompoundTag ct) {
                        long m = ((com.github.galysso.structures_features.duck.MobEffectInstanceDuck)(Object) value)
                                .getResponsibleStructure();
                        if (m != -1L) {
                            ct.putLong("responsibleStructure", m);
                        }
                    }
                    return out;
                });
            }
        };
    }

    @Inject(
        method = "setDetailsFrom",
        at = @At("TAIL"),
        require = 1
    )
    private void sf$copyMarker(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        this.responsibleStructure = ((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure();
    }

    @Inject(
        method = "update",
        at = @At("HEAD"),
        require = 1
    )
    private void structures_features$keepDominatedAtStructureEffectAddition(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        if (((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure() == -1L) return;

        MobEffectInstance thisEffect = (MobEffectInstance) (Object) this;
        boolean dominates = mobEffectInstance.getAmplifier() >= thisEffect.getAmplifier() && (
                mobEffectInstance.isInfiniteDuration()
                || mobEffectInstance.getDuration() <= thisEffect.getDuration()
            );
        if (!dominates) return;

        MobEffectInstance previous = new MobEffectInstance(thisEffect);

        ((MobEffectInstanceDuck) previous).setHidden(hiddenEffect);
        this.hiddenEffect = previous;
    }

    @Inject(
        method = "update",
        at = @At("HEAD"),
        require = 1
    )
    private void structures_features$allowAddingEffectDominatedByStructureEffect(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        MobEffectInstance thisEffect = (MobEffectInstance) (Object) this;
        if (((MobEffectInstanceDuck) thisEffect).getResponsibleStructure() == -1L) return;

        boolean dominates = mobEffectInstance.getAmplifier() <= thisEffect.getAmplifier() && (
                thisEffect.isInfiniteDuration()
                || mobEffectInstance.getDuration() >= thisEffect.getDuration()
            );
        if (!dominates) return;


        MobEffectInstance hidden = ((MobEffectInstanceDuck) thisEffect).getHidden();
        if (hidden == null) {
            this.hiddenEffect = mobEffectInstance;
        } else {
            hidden.update(mobEffectInstance);
        }
    }

    @Inject(
        method = "update",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;duration:I",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        ),
        require = 1
    )
    private void structures_features$copyMarkerUpdate(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        this.responsibleStructure = ((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure();
    }
}
