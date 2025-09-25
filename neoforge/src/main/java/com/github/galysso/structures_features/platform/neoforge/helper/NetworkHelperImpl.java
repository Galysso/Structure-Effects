package com.github.galysso.structures_features.platform.neoforge.helper;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.helper.NetworkHelper;
import com.github.galysso.structures_features.network.StructureFarewellPayload;
import com.github.galysso.structures_features.network.StructureWelcomePayload;
import com.github.galysso.structures_features.util.NetworkUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = StructuresFeatures.MOD_ID)
public final class NetworkHelperImpl implements NetworkHelper {

    /* ---- PACKETS DECLARATIONS ----- */

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        // Version de protocole propre à ton mod (même des deux côtés si tu as du serverbound)
        final PayloadRegistrar registrar = event.registrar("1");

        // Déclare: play -> client, codec + handler
        registrar.playToClient(StructureWelcomePayload.ID, StructureWelcomePayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveWelcome(Minecraft.getInstance(), payload.structureName()); });
        });

        registrar.playToClient(StructureFarewellPayload.ID, StructureFarewellPayload.CODEC, (payload, ctx) -> {
            ctx.enqueueWork(() -> { NetworkUtil.receiveFarewell(Minecraft.getInstance(), payload.structureName()); });
        });
    }

    /* ----- FROM SERVER TO CLIENT ----- */
    @Override
    public void sendWelcome(ServerPlayer player, String name) {
        PacketDistributor.sendToPlayer(player, new StructureWelcomePayload(name));
    }

    @Override
    public void sendFarewell(ServerPlayer player, String name) {
        PacketDistributor.sendToPlayer(player, new StructureFarewellPayload(name));
    }
}
