package com.github.galysso.structures_features.platform.neoforge.helper;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.neoforge.Compat_packetDistribution;
import com.github.galysso.structures_features.helper.NetworkHelper;
import com.github.galysso.structures_features.network.StructureFarewellPayload;
import com.github.galysso.structures_features.network.StructureWelcomePayload;
import com.github.galysso.structures_features.network.config_sync.ConfigClientFarewellPayload;
import com.github.galysso.structures_features.network.config_sync.ConfigClientWelcomePayload;
import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(
    modid = StructuresFeatures.MOD_ID,
    bus   = EventBusSubscriber.Bus.MOD
)
public final class NetworkHelperImpl implements NetworkHelper {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        // ----- S2C -----
        // Farewell
        registrar.playToClient(StructureFarewellPayload.ID, StructureFarewellPayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveFarewell(Minecraft.getInstance(), payload.structureName()); });
        });

        // Welcome
        registrar.playToClient(StructureWelcomePayload.ID, StructureWelcomePayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveWelcome(Minecraft.getInstance(), payload.structureName()); });
        });

        // ----- C2S -----
        // Farewell
        registrar.playToServer(ConfigClientFarewellPayload.ID, ConfigClientFarewellPayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveConfigFarewell((ServerPlayer) ctx.player(), payload.newValue()); });
        });

        // Welcome
        registrar.playToServer(ConfigClientWelcomePayload.ID, ConfigClientWelcomePayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveConfigWelcome((ServerPlayer) ctx.player(), payload.newValue()); });
        });
    }

    // ----- S2C -----
    // Farewell
    @Override
    public void sendFarewell(ServerPlayer player, String name) {
        PacketDistributor.sendToPlayer(player, new StructureFarewellPayload(name));
    }

    // Welcome
    @Override
    public void sendWelcome(ServerPlayer player, String name) {
        PacketDistributor.sendToPlayer(player, new StructureWelcomePayload(name));
    }

    // ----- C2S -----
    // Farewell
    @Override
    public void sendConfigFarewell(boolean newValue) {
        Compat_packetDistribution.sendToServer(new ConfigClientFarewellPayload(newValue));
    }

    // Welcome
    @Override
    public void sendConfigWelcome(boolean newValue) {
        Compat_packetDistribution.sendToServer(new ConfigClientWelcomePayload(newValue));
    }
}
