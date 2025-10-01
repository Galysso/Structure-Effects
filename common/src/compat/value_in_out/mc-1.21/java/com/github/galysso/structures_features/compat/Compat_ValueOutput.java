package com.github.galysso.structures_features.compat;

import com.mojang.serialization.Codec;

public class Compat_ValueOutput {
    public static <T> void store(Object valueOutput, String key, Codec<T> codec, T value) { }
}
