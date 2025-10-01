package com.github.galysso.structures_features.network.config_sync;

import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConfigClientWelcomePayload(boolean newValue) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigClientWelcomePayload> ID = new CustomPacketPayload.Type<>(NetworkUtil.CONFIG_CLIENT_WELCOME_PAYLOAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigClientWelcomePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigClientWelcomePayload::newValue,
            ConfigClientWelcomePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
