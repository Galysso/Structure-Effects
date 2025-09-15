package galysso.structures_features.network;

import galysso.structures_features.util.NetworkUtil;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record StructureNamePayload(String structureName, boolean entering) implements CustomPayload {
    public static final Id<StructureNamePayload> ID = new Id<>(NetworkUtil.STRUCTURE_NAME_PAYLOAD);

    public static final PacketCodec<RegistryByteBuf, StructureNamePayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, StructureNamePayload::structureName,
            PacketCodecs.BOOL, StructureNamePayload::entering,
            StructureNamePayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
