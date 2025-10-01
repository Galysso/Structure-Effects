package com.github.galysso.structures_features.config.server.elements;

public class EffectConfig {
    public String effectId;
    public int amplifier;
    public int duration;
    public boolean ambient;
    public boolean visible;
    public boolean showIcon;
    public boolean clearedWhenLeaving;

    public EffectConfig(
        String effectId,
        int amplifier,
        int duration,
        boolean ambient,
        boolean visible,
        boolean showIcon,
        boolean clearedWhenLeaving
    ) {
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.duration = duration;
        this.ambient = ambient;
        this.visible = visible;
        this.showIcon = showIcon;
        this.clearedWhenLeaving = clearedWhenLeaving;
    }

    public EffectConfig() {
        this(
            "minecraft:regeneration",
            0,
            0,
            true,
            true,
            true,
            true
        );
    }
}
