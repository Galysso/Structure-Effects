package com.github.galysso.structures_features.platform.fabric.helper;

import com.github.galysso.structures_features.helper.NetworkHelper;
import com.github.galysso.structures_features.network.StructureFarewellPayload;
import com.github.galysso.structures_features.network.StructureWelcomePayload;
import com.github.galysso.structures_features.network.config_sync.ConfigClientFarewellPayload;
import com.github.galysso.structures_features.network.config_sync.ConfigClientWelcomePayload;
import com.github.galysso.structures_features.util.NetworkUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkHelperImpl implements NetworkHelper {
    /* ---- PACKETS DECLARATIONS ----- */
    public static void init() {
        // ----- S2C -----
        PayloadTypeRegistry.playS2C().register(StructureFarewellPayload.ID, StructureFarewellPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StructureWelcomePayload.ID, StructureWelcomePayload.CODEC);

        /* FAREWELL */
        ClientPlayNetworking.registerGlobalReceiver(StructureFarewellPayload.ID, (payload, context) -> {
            context.client().execute(() -> {NetworkUtil.receiveFarewell(context.client(), payload.structureName());});
        });

        /* WELCOME */
        ClientPlayNetworking.registerGlobalReceiver(StructureWelcomePayload.ID, (payload, context) -> {
            context.client().execute(() -> {NetworkUtil.receiveWelcome(context.client(), payload.structureName());});
        });

        // ----- C2S -----
        //if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            PayloadTypeRegistry.playC2S().register(ConfigClientFarewellPayload.ID, ConfigClientFarewellPayload.CODEC);
            PayloadTypeRegistry.playC2S().register(ConfigClientWelcomePayload.ID, ConfigClientWelcomePayload.CODEC);

            // FAREWELL
            ServerPlayNetworking.registerGlobalReceiver(ConfigClientFarewellPayload.ID, (payload, context) -> {
                context.server().execute(() -> {
                    NetworkUtil.receiveConfigFarewell(context.player(), payload.newValue());
                });
            });

            // WELCOME
            ServerPlayNetworking.registerGlobalReceiver(ConfigClientWelcomePayload.ID, (payload, context) -> {
                context.server().execute(() -> {
                    NetworkUtil.receiveConfigWelcome(context.player(), payload.newValue());
                });
            });
        //}
    }

    /* ----- S2C ----- */
    @Override
    public void sendWelcome(ServerPlayer player, String name) {
        ServerPlayNetworking.send(player, new StructureWelcomePayload(name));
    }

    @Override
    public void sendFarewell(ServerPlayer player, String name) {
        ServerPlayNetworking.send(player, new StructureFarewellPayload(name));
    }

    /* ----- C2S ----- */
    @Override
    public void sendConfigFarewell(boolean newValue) {
        ClientPlayNetworking.send(new ConfigClientFarewellPayload(newValue));
    }

    @Override
    public void sendConfigWelcome(boolean newValue) {
        ClientPlayNetworking.send(new ConfigClientWelcomePayload(newValue));
    }
}
