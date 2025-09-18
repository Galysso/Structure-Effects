package com.github.galysso.structures_features.network;

import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StructureFarewellPayload(String structureName) implements CustomPacketPayload {
    public static final Type<StructureFarewellPayload> ID = new Type<>(NetworkUtil.STRUCTURE_FAREWELL_PAYLOAD);

    public static final StreamCodec<RegistryFriendlyByteBuf, StructureFarewellPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, StructureFarewellPayload::structureName,
            StructureFarewellPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
