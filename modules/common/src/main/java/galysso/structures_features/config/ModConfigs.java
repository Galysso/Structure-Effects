package galysso.structures_features.config;

import java.util.concurrent.atomic.AtomicReference;

public final class ModConfigs {
    private static final AtomicReference<ServerConfigData> SERVER =
            new AtomicReference<>(ServerConfigData.defaults());

    public static ServerConfigData server() { return SERVER.get(); }
    public static void setServer(ServerConfigData cfg) { SERVER.set(cfg); }

    private ModConfigs() {}
}