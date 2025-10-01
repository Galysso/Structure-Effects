package com.github.galysso.structures_features.config.server;

import com.github.galysso.structures_features.StructuresFeatures;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import org.jetbrains.annotations.NotNull;

@ConvertFrom(fileName = "server/server_integrations.toml", folder = "structures_features", subfolder = "server")
public class ServerIntegrationsConfig extends Config {
    public ServerIntegrationsConfig() {
        super(StructuresFeatures.identifier("server/server_integrations"));
    }

    @Override @NotNull
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public boolean wraith_waystones = true;
}
