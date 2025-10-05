package com.github.galysso.structures_features.config.server.elements;

public class EffectConfig {
    public int amplifier;
    public int duration;
    public boolean ambient;
    public boolean visible;
    public boolean showIcon;
    public boolean clearedWhenLeaving;

    public EffectConfig(
        int amplifier,
        int duration,
        boolean ambient,
        boolean visible,
        boolean showIcon,
        boolean clearedWhenLeaving
    ) {
        this.amplifier = amplifier;
        this.duration = duration;
        this.ambient = ambient;
        this.visible = visible;
        this.showIcon = showIcon;
        this.clearedWhenLeaving = clearedWhenLeaving;
    }

    public EffectConfig() {
        this(
            0,
            60,
            true,
            true,
            true,
            true
        );
    }

    public boolean isInfiniteDuration() {
        return duration < 0;
    }
}
