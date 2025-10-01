package com.github.galysso.structures_features.util;

import com.github.galysso.structures_features.StructuresFeatures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class NetworkUtil {
    public static final ResourceLocation STRUCTURE_WELCOME_PAYLOAD = StructuresFeatures.identifier("structure_welcome_payload");
    public static final ResourceLocation STRUCTURE_FAREWELL_PAYLOAD = StructuresFeatures.identifier("structure_farewell_payload");

    public static void receiveWelcome(Minecraft client, String name) {
        if (!StructuresFeatures.CLIENT_CONFIG.displayWelcomes || name == null || name.isEmpty()) return;

        client.gui.setOverlayMessage(
            Component.translatable("structures_features.message_prefix.entering").append(name).withStyle(ChatFormatting.DARK_GREEN),
            false
        );
    }

    public static void receiveFarewell(Minecraft client, String name) {
        if (!StructuresFeatures.CLIENT_CONFIG.displayFarewells || name == null || name.isEmpty()) return;

        client.gui.setOverlayMessage(
            Component.translatable("structures_features.message_prefix.leaving").append(name).withStyle(ChatFormatting.DARK_RED),
            false
        );
    }
}
