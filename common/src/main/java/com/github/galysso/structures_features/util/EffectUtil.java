package com.github.galysso.structures_features.util;

import com.github.galysso.structures_features.compat.Compat_Registry;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;

public class EffectUtil {
    public static Optional<Holder<MobEffect>> getMobEffectHolder(String effectId) {
        ResourceLocation id = ResourceLocation.tryParse(effectId);
        if (id == null) return Optional.empty();

        Registry<MobEffect> reg = Compat_Registry.getRegistry(ServerAccessor.getOverworld(), Registries.MOB_EFFECT);
        ResourceKey<MobEffect> key = ResourceKey.create(Registries.MOB_EFFECT, id);

        Holder<MobEffect> holder = Compat_Registry.getHolder(reg, key).orElse(null);
        if (holder == null) return Optional.empty();

        return Optional.of(holder);
    }

    public static Optional<MobEffectInstance> getMobEffectInstance(ServerPlayer player, String effectId) {
        Optional<Holder<MobEffect>> holderOpt = getMobEffectHolder(effectId);
        if (holderOpt.isEmpty()) return Optional.empty();
        return getMobEffectInstance(player, holderOpt.get());
    }

    public static Optional<MobEffectInstance> getMobEffectInstance(ServerPlayer player, Holder<MobEffect> holder) {
        MobEffectInstance instance = player.getEffect(holder);
        return instance == null ? Optional.empty() : Optional.of(instance);
    }
}
