package com.github.galysso.structures_features.compat;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueOutput;

public class Compat_ValueOutput {
    public static <T> void store(Object valueOutput, String key, Codec<T> codec, T value) {
        if (!(valueOutput instanceof ValueOutput)) {
            throw new IllegalArgumentException("Expected ValueOutput, got: " + valueOutput.getClass());
        }
        ((ValueOutput) valueOutput).store(key, codec, value);
    }
}
