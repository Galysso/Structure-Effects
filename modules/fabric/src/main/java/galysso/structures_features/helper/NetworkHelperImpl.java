package galysso.structures_features.helper;

import galysso.structures_features.network.StructureFarewellPayload;
import galysso.structures_features.network.StructureWelcomePayload;
import galysso.structures_features.util.NetworkUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class NetworkHelperImpl implements NetworkHelper {

    /* ---- PACKETS DECLARATIONS ----- */
    public static void init() {
        PayloadTypeRegistry.playS2C().register(StructureWelcomePayload.ID, StructureWelcomePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StructureFarewellPayload.ID, StructureFarewellPayload.CODEC);

        /* WELCOME */
        ClientPlayNetworking.registerGlobalReceiver(StructureWelcomePayload.ID, (payload, context) -> {
            context.client().execute(() -> {NetworkUtil.receiveWelcome(context.client(), payload.structureName());});
        });

        /* FAREWELL */
        ClientPlayNetworking.registerGlobalReceiver(StructureFarewellPayload.ID, (payload, context) -> {
            context.client().execute(() -> {NetworkUtil.receiveFarewell(context.client(), payload.structureName());});
        });
    }

    /* ----- FROM SERVER TO CLIENT ----- */
    @Override
    public void sendWelcome(ServerPlayerEntity player, String name) {
        ServerPlayNetworking.send(player, new StructureWelcomePayload(name));
    }

    @Override
    public void sendFarewell(ServerPlayerEntity player, String name) {
        ServerPlayNetworking.send(player, new StructureFarewellPayload(name));
    }
}
