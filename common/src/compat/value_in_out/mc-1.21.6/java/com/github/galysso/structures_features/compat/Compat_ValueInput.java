package com.github.galysso.structures_features.compat;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.ValueInput;

import java.util.Optional;

public class Compat_ValueInput {
    public static <T> Optional<T> read(Object valueInput, String key, Codec<T> codec) {
        if (!(valueInput instanceof ValueInput)) {
            throw new IllegalArgumentException("Expected ValueInput, got: " + valueInput.getClass());
        }
        return ((ValueInput) valueInput).read(key, codec);
    }
}
