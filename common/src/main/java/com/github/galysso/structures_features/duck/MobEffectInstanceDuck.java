package com.github.galysso.structures_features.duck;

import net.minecraft.world.effect.MobEffectInstance;

public interface MobEffectInstanceDuck {
    MobEffectInstance getHidden();
    void setResponsibleStructure(long structureId);
    long getResponsibleStructure();
}
