package com.github.galysso.structures_features.config.client;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.helper.PlatformLoader;
import com.github.galysso.structures_features.network.config_sync.ConfigClientWelcomePayload;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.ValidatedField;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedBoolean;

import java.util.function.Supplier;

@ConvertFrom(fileName = "client/client.toml", folder = "structures_features", subfolder = "client")
public class ClientConfig extends Config {
    public ClientConfig() {
        super(StructuresFeatures.identifier("client/client"));
    }

    public ValidatedBoolean displayFarewells; {
        ValidatedBoolean myBool = new ValidatedBoolean(true);
        myBool.listenToEntry(newValue -> PlatformLoader.sendConfigFarewell(newValue.get()));
        displayFarewells = myBool;
    }

    public ValidatedBoolean displayWelcomes; {
        ValidatedBoolean myBool = new ValidatedBoolean(true);
        myBool.listenToEntry(newValue -> PlatformLoader.sendConfigWelcome(newValue.get()));
        displayWelcomes = myBool;
    }
}
