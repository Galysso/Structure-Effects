package com.github.galysso.structures_features.compat;

import com.mojang.serialization.Codec;

import java.util.Optional;

public class Compat_ValueInput {
    public static <T> Optional<T> read(Object valueInput, String key, Codec<T> codec) {
        return Optional.empty();
    }
}
