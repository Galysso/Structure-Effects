package com.github.galysso.structures_features.helper;

import com.github.galysso.structures_features.config.server.util.ClientConfigTracker;
import net.minecraft.server.level.ServerPlayer;
import java.util.ServiceLoader;

public final class PlatformLoader {
    private PlatformLoader() {}
    private static final NetworkHelper IMPL =
            ServiceLoader.load(NetworkHelper.class, PlatformLoader.class.getClassLoader())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No NetworkHelper impl found"));

    // ----- S2C -----
    public static void sendFarewell(ServerPlayer player, String name) {
        if (name == null || name.isEmpty()) return;

        if (ClientConfigTracker.expectsFarewellMessage(player)) {
            System.out.println("Sending farewell to " + player.getName().getString() + " for structure " + name);
            IMPL.sendFarewell(player, name);
        } else {
            System.out.println("Not sending farewell to " + player.getName().getString() + " for structure " + name + " (not expected)");
        }
    }

    public static void sendWelcome(ServerPlayer player, String name) {
        if (name == null || name.isEmpty()) return;

        if (ClientConfigTracker.expectsWelcomeMessage(player)) {
            System.out.println("Sending welcome to " + player.getName().getString() + " for structure " + name);
            IMPL.sendWelcome(player, name);
        } else {
            System.out.println("Not sending welcome to " + player.getName().getString() + " for structure " + name + " (not expected)");
        }
    }

    // ----- C2S -----
    public static void sendConfigFarewell(boolean newValue) {
        System.out.println("Sending config farewell: " + newValue);
        IMPL.sendConfigFarewell(newValue);
    }

    public static void sendConfigWelcome(boolean newValue) {
        System.out.println("Sending config welcome: " + newValue);
        IMPL.sendConfigWelcome(newValue);
    }
}
