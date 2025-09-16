package galysso.structures_features.network;

import galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StructureWelcomePayload(String structureName) implements CustomPayload {
    public static final Id<StructureWelcomePayload> ID = new Id<>(NetworkUtil.STRUCTURE_WELCOME_PAYLOAD);

    public static final PacketCodec<RegistryByteBuf, StructureWelcomePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StructureWelcomePayload::structureName,
            StructureWelcomePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
