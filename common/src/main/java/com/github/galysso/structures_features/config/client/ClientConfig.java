package com.github.galysso.structures_features.config.client;

import com.github.galysso.structures_features.StructuresFeatures;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;

@ConvertFrom(fileName = "client/client.toml", folder = "structures_features", subfolder = "client")
public class ClientConfig extends Config {
    public ClientConfig() {
        super(StructuresFeatures.identifier("client/client"));
    }

    public boolean displayWelcomes = true;
    public boolean displayFarewells = true;
}
