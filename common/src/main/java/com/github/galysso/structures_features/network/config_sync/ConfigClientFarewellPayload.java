package com.github.galysso.structures_features.network.config_sync;

import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ConfigClientFarewellPayload(boolean newValue) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ConfigClientFarewellPayload> ID = new CustomPacketPayload.Type<>(NetworkUtil.CONFIG_CLIENT_FAREWELL_PAYLOAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, ConfigClientFarewellPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ConfigClientFarewellPayload::newValue,
            ConfigClientFarewellPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
