package com.github.galysso.structures_features.helper;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper {
    void sendWelcome(ServerPlayer player, String name);
    void sendFarewell(ServerPlayer player, String name);
}