package com.github.galysso.structures_features.config.server.util;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientConfigTracker {
    private static Map<UUID, Boolean> receiveFarewellMessages = new HashMap<>();
    private static Map<UUID, Boolean> receiveWelcomeMessages = new HashMap<>();

    public static Boolean expectsFarewellMessage(ServerPlayer player) {
        return receiveFarewellMessages.getOrDefault(player.getUUID(), false);
    }

    public static Boolean expectsWelcomeMessage(ServerPlayer player) {
        return receiveWelcomeMessages.getOrDefault(player.getUUID(), false);
    }

    public static void updateFarewellConfig(ServerPlayer player, boolean newValue) {
        System.out.println("Updating farewell config: " + newValue);
        receiveFarewellMessages.put(player.getUUID(), newValue);
    }

    public static void updateWelcomeConfig(ServerPlayer player, boolean newValue) {
        System.out.println("Updating welcome config: " + newValue);
        receiveWelcomeMessages.put(player.getUUID(), newValue);
    }
}
