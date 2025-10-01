package com.github.galysso.structures_features.helper;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkHelper {
    // ----- S2C -----
    void sendFarewell(ServerPlayer player, String name);
    void sendWelcome(ServerPlayer player, String name);

    // ----- C2S -----
    void sendConfigFarewell(boolean newValue);
    void sendConfigWelcome(boolean newValue);
}