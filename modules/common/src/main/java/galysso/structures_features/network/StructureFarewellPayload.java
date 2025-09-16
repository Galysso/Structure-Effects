package galysso.structures_features.network;

import galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StructureFarewellPayload(String structureName) implements CustomPayload {
    public static final Id<StructureFarewellPayload> ID = new Id<>(NetworkUtil.STRUCTURE_FAREWELL_PAYLOAD);

    public static final PacketCodec<RegistryByteBuf, StructureFarewellPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StructureFarewellPayload::structureName,
            StructureFarewellPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
