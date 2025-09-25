package com.github.galysso.structures_features.platform.fabric.helperImpl;

import com.github.galysso.structures_features.helper.NetworkHelper;
import com.github.galysso.structures_features.network.StructureFarewellPayload;
import com.github.galysso.structures_features.network.StructureWelcomePayload;
import com.github.galysso.structures_features.util.NetworkUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

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
    public void sendWelcome(ServerPlayer player, String name) {
        ServerPlayNetworking.send(player, new StructureWelcomePayload(name));
    }

    @Override
    public void sendFarewell(ServerPlayer player, String name) {
        ServerPlayNetworking.send(player, new StructureFarewellPayload(name));
    }
}
