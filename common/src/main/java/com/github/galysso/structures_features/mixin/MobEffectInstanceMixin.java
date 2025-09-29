package com.github.galysso.structures_features.mixin;

import com.github.galysso.structures_features.duck.MobEffectInstanceDuck;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MobEffectInstance.class)
public abstract class MobEffectInstanceMixin implements MobEffectInstanceDuck {
    @Shadow
    private MobEffectInstance hiddenEffect;

    @Override
    public MobEffectInstance getHidden() {
        return this.hiddenEffect;
    }
}
