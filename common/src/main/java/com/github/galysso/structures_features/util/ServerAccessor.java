package com.github.galysso.structures_features.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

public class ServerAccessor {
    private static MinecraftServer serverInstance;

    public static void setServerInstance(MinecraftServer server) {
        serverInstance = server;
    }

    public static MinecraftServer getServer() {
        return serverInstance;
    }

    public static ServerLevel getOverworld() {
        return serverInstance.overworld();
    }

    public static long getGameTime() {
        return serverInstance.overworld().getGameTime();
    }

    public static boolean ready() {
        return serverInstance != null;
    }
}
