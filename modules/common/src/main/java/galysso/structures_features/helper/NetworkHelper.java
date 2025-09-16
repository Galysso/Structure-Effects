package galysso.structures_features.helper;

import net.minecraft.server.network.ServerPlayerEntity;

public interface NetworkHelper {
    void sendWelcome(ServerPlayerEntity player, String name);
    void sendFarewell(ServerPlayerEntity player, String name);
}