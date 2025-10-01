package com.github.galysso.structures_features.util;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.config.server.util.ClientConfigTracker;
import com.github.galysso.structures_features.helper.PlatformLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class NetworkUtil {
    // S2C
    public static final ResourceLocation STRUCTURE_FAREWELL_PAYLOAD = StructuresFeatures.identifier("structure_farewell_payload");
    public static final ResourceLocation STRUCTURE_WELCOME_PAYLOAD = StructuresFeatures.identifier("structure_welcome_payload");

    public static void receiveFarewell(Minecraft client, String name) {
        if (name == null || name.isEmpty()) return;

        if (!StructuresFeatures.CLIENT_CONFIG.displayFarewells.get()) {
            PlatformLoader.sendConfigFarewell(false);
        }

        client.gui.setOverlayMessage(
                Component.translatable("structures_features.message_prefix.leaving").append(name).withStyle(ChatFormatting.DARK_RED),
                false
        );
    }

    public static void receiveWelcome(Minecraft client, String name) {
        if (name == null || name.isEmpty()) return;

        if (!StructuresFeatures.CLIENT_CONFIG.displayWelcomes.get()) {
            PlatformLoader.sendConfigWelcome(false);
        }

        client.gui.setOverlayMessage(
            Component.translatable("structures_features.message_prefix.entering").append(name).withStyle(ChatFormatting.DARK_GREEN),
            false
        );
    }

    // C2S
    public static final ResourceLocation CONFIG_CLIENT_FAREWELL_PAYLOAD = StructuresFeatures.identifier("config_client_farewell_payload");
    public static final ResourceLocation CONFIG_CLIENT_WELCOME_PAYLOAD = StructuresFeatures.identifier("config_client_welcome_payload");

    public static void receiveConfigFarewell(ServerPlayer player, boolean newValue) {
        ClientConfigTracker.updateFarewellConfig(player, newValue);
    }

    public static void receiveConfigWelcome(ServerPlayer player, boolean newValue) {
        ClientConfigTracker.updateWelcomeConfig(player, newValue);
    }
}
