package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffectInstance;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceDuck {
    @Shadow
    private MobEffectInstance hiddenEffect;

    @Unique
    private long responsibleStructure = -1;

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

    @Inject(
        method = "save()Lnet/minecraft/nbt/Tag;",
        at = @At("RETURN"),
        require = 1
    )
    private void sf$saveMarker(CallbackInfoReturnable<Tag> cir) {
        Tag ret = cir.getReturnValue();
        if (ret instanceof CompoundTag ct) {
            ct.putLong("responsibleStructure", responsibleStructure);
        }
    }

    @Inject(
        method = "load(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/world/effect/MobEffectInstance;",
        at = @At("RETURN"),
        require = 1
    )
    private static void sf$loadMarker(CompoundTag input, CallbackInfoReturnable<MobEffectInstance> cir) {
        MobEffectInstance inst = cir.getReturnValue();
        if (inst != null) {
            if (input.contains("responsibleStructure", Tag.TAG_LONG)) {
                ((MobEffectInstanceDuck) (Object) inst).setResponsibleStructure(input.getLong("responsibleStructure"));
            } else {
                ((MobEffectInstanceDuck) (Object) inst).setResponsibleStructure(-1L);
            }
        }
    }

    @Inject(
        method = "setDetailsFrom",
        at = @At("TAIL")
    )
    private void sf$copyMarker(MobEffectInstance mobEffectInstance, CallbackInfo ci) {
        this.responsibleStructure = ((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure();
    }

    @Inject(
        method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
        at = @At("HEAD")
    )
    private void structures_features$keepRelevantHiddenEffects(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        if (((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure() == -1L) return;


        MobEffectInstance thisEffect = (MobEffectInstance) (Object) this;
        boolean dominates = !thisEffect.isInfiniteDuration() && (
                    mobEffectInstance.isInfiniteDuration()
                    || mobEffectInstance.getAmplifier() >= thisEffect.getAmplifier()
                );
        if (!dominates) return;

        MobEffectInstance previous = new MobEffectInstance(thisEffect);

        ((MobEffectInstanceDuck) previous).setHidden(hiddenEffect);
        this.hiddenEffect = previous;
    }

    @Inject(
        method = "update(Lnet/minecraft/world/effect/MobEffectInstance;)Z",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/world/effect/MobEffectInstance;duration:I",
            opcode = Opcodes.PUTFIELD,
            shift = At.Shift.AFTER
        )
    )
    private void structures_features$copyMarkerUpdate(MobEffectInstance mobEffectInstance, CallbackInfoReturnable<Boolean> cir) {
        this.responsibleStructure = ((MobEffectInstanceDuck) mobEffectInstance).getResponsibleStructure();
    }
}
