package com.github.galysso.structures_features.network;

import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StructureWelcomePayload(String structureName) implements CustomPacketPayload {
    public static final Type<StructureWelcomePayload> ID = new Type<>(NetworkUtil.STRUCTURE_WELCOME_PAYLOAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureWelcomePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StructureWelcomePayload::structureName,
            StructureWelcomePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
