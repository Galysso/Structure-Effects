package galysso.structures_features.helper;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.ServiceLoader;

public final class PlatformLoader {
    private PlatformLoader() {}
    private static final NetworkHelper IMPL =
            ServiceLoader.load(NetworkHelper.class, PlatformLoader.class.getClassLoader())
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No NetworkHelper impl found"));

    public static void sendWelcome(ServerPlayerEntity player, String name) {
        IMPL.sendWelcome(player, name);
    }

    public static void sendFarewell(ServerPlayerEntity player, String name) {IMPL.sendFarewell(player, name);}
}
