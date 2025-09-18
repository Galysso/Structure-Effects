package com.github.galysso.structures_features.config;

import java.util.concurrent.atomic.AtomicReference;

public final class ModConfigs {
    private static final AtomicReference<ServerConfigData> SERVER_NAMES_SETS =
            new AtomicReference<>(ServerConfigData.defaults());

    public static ServerConfigData server() { return SERVER_NAMES_SETS.get(); }
    public static void setServerNamesSets(ServerConfigData cfg) { SERVER_NAMES_SETS.set(cfg); }

    private ModConfigs() {}
}